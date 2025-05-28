import pytz
import requests
from datetime import datetime, timedelta, timezone, time


def should_update_prices(et_now, last_update_date):
    after_noon = (et_now.hour > 12) or (et_now.hour == 12 and et_now.minute >= 1 and et_now.second > 0)
    return after_noon and (last_update_date.date() < et_now.date())

def get_et_now():
    return (datetime.now(timezone.utc)
            .astimezone(timezone(timedelta(hours=-4)))
            .replace(microsecond=0))

def get_time_until_next_midday(et_now):
    if et_now.hour < 12 or (et_now.hour == 12 and et_now.minute == 0):
        next_midday = (et_now
                       .replace(hour=12, minute=0, second=0, microsecond=0))
    else:
        next_midday = ((et_now + timedelta(days=1))
                       .replace(hour=12, minute=0, second=0, microsecond=0))

    return str(next_midday - et_now).split(".")[0]

def update_prices_if_needed(price_cache, last_update_date, pairs, et_now):
    if not price_cache or should_update_prices(et_now, last_update_date):
        price_cache.clear()
        price_cache.update(get_close_prices(pairs))
        last_update_date = get_et_now()

    return price_cache, last_update_date

def get_close_prices(pairs):
    timestamp_ms = get_latest_12_00_pm_et_timestamp()

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


def get_latest_12_00_pm_et_timestamp():
    et_tz = pytz.timezone('US/Eastern')
    now_et = datetime.now(et_tz)

    today_12_pm = et_tz.localize(datetime.combine(now_et.date(), time(12, 0)))

    if now_et >= today_12_pm:
        latest = today_12_pm
    else:
        yesterday = now_et.date() - timedelta(days=1)
        latest = et_tz.localize(datetime.combine(yesterday, time(12, 0)))

    return int(latest.timestamp() * 1000)