from datetime import datetime, timezone
import requests
from datetime import timedelta

def should_update_prices(et_now, last_update_date):
    return et_now.hour == 12 and et_now.minute == 1 and (last_update_date != et_now.date())

def get_time_until_next_midday(et_now):
    if et_now.hour < 12 or (et_now.hour == 12 and et_now.minute == 0):
        next_midday = et_now.replace(hour=12, minute=0, second=0, microsecond=0)
    else:
        next_midday = (et_now + timedelta(days=1)).replace(hour=12, minute=0, second=0, microsecond=0)
    return str(next_midday - et_now).split(".")[0]

def get_close_prices(pairs, target_date):
    dt = datetime.strptime(target_date, "%Y-%m-%d %H:%M") - timedelta(hours=-4)
    dt = dt.replace(tzinfo=timezone.utc)
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
        data = response.json()
        if data:
            close_price = float(data[0][4])
            result[pair] = round(close_price, 3)
        else:
            result[pair] = "No data available."
    return result

def get_et_now():
    return datetime.now(timezone.utc).astimezone(timezone(timedelta(hours=-4))).replace(microsecond=0)