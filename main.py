from fastapi import FastAPI, HTTPException
from fastapi.responses import HTMLResponse
from pydantic import BaseModel
from datetime import datetime, timedelta, timezone
from apscheduler.schedulers.background import BackgroundScheduler
import requests
import pytz

app = FastAPI()

# Cache to store prices
price_cache = {}

class PriceRequest(BaseModel):
    pairs: list[str]
    target_date: str  # Format: "YYYY-MM-DD HH:MM"

def get_close_prices(pairs, target_date):
    dt = datetime.strptime(target_date, "%Y-%m-%d %H:%M").replace(tzinfo=timezone.utc)
    timestamp_ms = int(dt.timestamp() * 1000)

    url = "https://api.binance.com/api/v3/klines"
    result = {}

    for pair in pairs:
        params = {
            "symbol": pair.replace("_", ""),
            "interval": "1m",
            "startTime": timestamp_ms,
            "limit": 1
        }

        response = requests.get(url, params=params)
        if response.status_code != 200:
            raise HTTPException(status_code=500, detail=f"Error fetching data for {pair}")

        data = response.json()

        if data:
            close_price = float(data[0][4])
            result[pair] = round(close_price, 3)
        else:
            result[pair] = "No data available."

    return result

def fetch_and_cache_prices():
    """Fetch the last available 12:00 ET price and cache it."""
    utc_now = datetime.now(timezone.utc)
    et_tz = pytz.timezone("America/New_York")
    et_now = utc_now.astimezone(et_tz)

    # Calculate the most recent 12:00 ET timestamp
    if et_now.hour < 12:
        # If before 12:00 ET, use the previous day's 12:00
        target_date = (et_now - timedelta(days=1)).replace(hour=12, minute=0, second=0, microsecond=0)
    else:
        # If 12:00 ET or later, use today's 12:00
        target_date = et_now.replace(hour=12, minute=0, second=0, microsecond=0)

    # Convert target_date to string format
    target_date_str = target_date.strftime("%Y-%m-%d %H:%M")

    pairs = ["BTC_USDT", "ETH_USDT", "SOL_USDT"]

    try:
        prices = get_close_prices(pairs, target_date_str)
        price_cache["latest_prices"] = {
            "timestamp": target_date_str,
            "prices": prices
        }
    except Exception as e:
        print(f"Error updating prices: {e}")

@app.get("/", response_class=HTMLResponse)
def fetch_default_close_prices_html():
    """Return an HTML page with the current time, time remaining for the next bet, and a 3x2 grid of asset prices."""
    if "latest_prices" not in price_cache:
        fetch_and_cache_prices()

    prices = price_cache["latest_prices"]["prices"]
    timestamp = price_cache["latest_prices"]["timestamp"]

    # Convert timestamp to ET time
    utc_now = datetime.now(timezone.utc)
    et_tz = pytz.timezone("America/New_York")
    et_now = utc_now.astimezone(et_tz)
    et_time_str = et_now.strftime("%H:%M:%S")

    # Calculate time remaining for the next 12:00 ET
    next_bet_time = et_now.replace(hour=12, minute=0, second=0, microsecond=0)
    if et_now.hour >= 12:
        next_bet_time += timedelta(days=1)
    time_remaining = next_bet_time - et_now
    time_remaining_str = f"{time_remaining.seconds // 3600:02}:{(time_remaining.seconds // 60) % 60:02}"

    # Generate HTML content
    html_content = f"""
    <!DOCTYPE html>
    <html>
    <head>
        <title>Over under polymarket bet</title>
        <style>
            body {{
                font-family: Arial, sans-serif;
                margin: 20px;
            }}
            .grid {{
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 10px;
                max-width: 400px;
                margin: 0 auto;
            }}
            .grid-item {{
                padding: 10px;
                border: 1px solid #ccc;
                text-align: center;
                background-color: #f9f9f9;
            }}
            .header {{
                text-align: center;
                margin-bottom: 20px;
            }}
        </style>
    </head>
    <body>
        <div class="header">
            <h1>Last Asset Prices at 12:00 ET</h1>
            <p>Current time: {et_time_str}</p>
            <p>Time remaining for the next bet: {time_remaining_str}</p>
        </div>
        <div class="grid">
    """

    # Add asset names and prices to the grid
    for asset, price in prices.items():
        html_content += f"""
            <div class="grid-item">{asset}</div>
            <div class="grid-item">{price}</div>
        """

    html_content += """
        </div>
    </body>
    </html>
    """

    return HTMLResponse(content=html_content)

# Scheduler setup
scheduler = BackgroundScheduler()
scheduler.add_job(fetch_and_cache_prices, "cron", hour=12, minute=2, timezone="America/New_York")
scheduler.start()

# Shutdown the scheduler when the app stops
@app.on_event("shutdown")
def shutdown_event():
    scheduler.shutdown()