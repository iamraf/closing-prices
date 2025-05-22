from anyio import current_time
from fastapi import FastAPI
from fastapi.responses import HTMLResponse
from pydantic import BaseModel
from datetime import datetime, timedelta, timezone
import requests
import pytz

app = FastAPI()

price_cache = {}

class PriceRequest(BaseModel):
    pairs: list[str]
    target_date: str

def get_close_prices(pairs, target_date):
    dt = datetime.strptime(target_date, "%Y-%m-%d %H:%M") - timedelta(hours=-4)
    dt = dt.replace(tzinfo=timezone.utc)
    timestamp_ms = int(dt.timestamp() * 1000)
    print(f"Fetching prices for {pairs} at {target_date} , timestamp: {timestamp_ms}")
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
        data = response.json()

        if data:
            close_price = float(data[0][4])
            result[pair] = round(close_price, 3)
        else:
            result[pair] = "No data available."

    return result


@app.get("/", response_class=HTMLResponse)
def fetch_default_close_prices_html():
    utc_now = datetime.now(timezone.utc)
    et_tz = pytz.timezone("America/New_York")
    et_now = utc_now.astimezone(et_tz)

    # Calculate the next midnight in ET
    if et_now.hour < 12 or (et_now.hour == 12 and et_now.minute == 0):
        next_midday = et_now.replace(hour=12, minute=0, second=0, microsecond=0)
    else:
        next_midday = (et_now + timedelta(days=1)).replace(hour=12, minute=0, second=0, microsecond=0)

    time_remaining = next_midday - et_now

    time_remaining = str(time_remaining).split(".")[0]  # Remove microseconds

    if et_now.hour < 12 or (et_now.hour == 12 and et_now.minute < 1):
        target_date = (et_now - timedelta(days=1)).strftime("%Y-%m-%d 12:00")
    else:
        target_date = et_now.strftime("%Y-%m-%d 12:00")


    pairs = ["BTC_USDT", "ETH_USDT", "SOL_USDT"]
    close_prices = get_close_prices(pairs, target_date)

    time_only = et_now.strftime("%H:%M:%S")

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
            <p>Current time: {time_only} ET</p>
            <p>Time remaining for the next bet: {time_remaining}</p>
        </div>
        <div class="grid">
    """

    for asset, price in close_prices.items():
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
