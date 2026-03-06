![Cryptocurrency](https://cdn.rabbit-company.com/plugins/CryptoCurrency/banner5.png)

## Plugin Description

CryptoCurrency is a Minecraft plugin that brings the world of cryptocurrencies into your server. It allows players to interact with virtual versions of real-world digital currencies directly in-game, creating a more dynamic and engaging economy.

## Compatibility

CryptoCurrency supports Minecraft server versions **from 1.8 to the latest version**.

## Features

- **Real-time Crypto Prices**  
The plugin fetches up-to-date prices from popular cryptocurrency exchanges such as Coinbase and Binance. Players can check accurate, real-time prices of their favorite cryptocurrencies directly in Minecraft.

- **In-Game Trading**  
With Vault integration, CryptoCurrency allows players to buy, sell, and send cryptocurrencies to other players. This creates a dynamic in-game economy powered by digital currencies.

- **Sign Shop Support**  
Players can create shops that accept cryptocurrencies as payment, making it possible to buy and sell items using virtual crypto wallets. This helps build a thriving player-driven marketplace.

- **Mining Rewards**  
Players can earn cryptocurrencies by mining blocks such as diamond ore, gold ore, iron ore, and more. This adds an extra reward system to the mining experience.

- **Configurability**  
Sever administrators have full control over plugin settings, including supported cryptocurrencies, exchange sources, mining rewards, and more.

- **MySQL Database Support**  
MySQL support allows multiple Minecraft servers to share player balances, making cryptocurrency holdings synchronized across server instances.

Step into the world of cryptocurrencies and revolutionize your Minecraft server with real-time prices, immersive trading, and a unique in-game economy.

> **Note:** This plugin does not involve real-world financial transactions or actual ownership of cryptocurrencies. It is only a Minecraft plugin that simulates and integrates virtual representations of digital currencies for in-game use.

## Default Cryptocurrencies

- `btc` - Bitcoin
- `bch` - Bitcoin Cash
- `eth` - Ethereum
- `etc` - Ethereum Classic
- `doge` - Dogecoin
- `ltc` - Litecoin
- `usdt` - Tether

More cryptocurrencies can be added in `cryptocurrencies.yml`.

## Commands

- `/<crypto> help`
- `/<crypto> price`
- `/<crypto> price <amount>`
- `/<crypto> balance`
- `/<crypto> balance <player>`
- `/<crypto> buy <amount>`
- `/<crypto> sell <amount>`
- `/<crypto> send <player> <amount>`
- `/<crypto> give <player> <amount>`
- `/<crypto> take <player> <amount>`
- `/<crypto> reload`

## Permissions

### General

- `cryptocurrency.help`  
	Allow players to see all available commands  
	**Default:** `true`

- `cryptocurrency.reload`  
	Reload the plugin configuration  
	**Default:** `op`

- `cryptocurrency.price`  
	Allow players to see current crypto prices  
	**Default:** `true`

### Balance

- `cryptocurrency.balance`  
	Allow players to see their crypto balance  
	**Default:** `true`

- `cryptocurrency.balance.other`  
	Allow players to see crypto balance of other players  
	**Default:** `op`

### Trading

- `cryptocurrency.buy`  
	Allow players to buy cryptocurrencies  
	**Default:** `true`

- `cryptocurrency.sell`  
	Allow players to sell cryptocurrencies  
	**Default:** `true`

- `cryptocurrency.send`  
	Allow players to send their cryptocurrencies to other players  
	**Default:** `true`

### Admin

- `cryptocurrency.give`  
	Allow players to give cryptocurrencies to other players  
	**Default:** `op`

- `cryptocurrency.take`  
	Allow players to take cryptocurrencies from other players  
	**Default:** `op`

### Shops

- `cryptocurrency.shop.create`  
	Allow players to create crypto sign shops  
	**Default:** `true`

- `cryptocurrency.shop.create.admin`  
	Allow players to create admin crypto sign shops  
	**Default:** `op`

- `cryptocurrency.shop.use`  
	Allow players to use crypto sign shops  
	**Default:** `true`

- `cryptocurrency.shop.use.admin`  
	Allow players to use admin crypto sign shops  
	**Default:** `true`

- `cryptocurrency.shop.break`  
	Allow players to break their own crypto sign shops  
	**Default:** `true`

- `cryptocurrency.shop.break.other`  
	Allow players to break crypto sign shops from other players  
	**Default:** `op`

### Mining

- `cryptocurrency.mining`  
	Allow players to earn cryptocurrencies when mining blocks  
	**Default:** `true`

## Placeholders

- `%cryptocurrency_<crypto>_balance%`
- `%cryptocurrency_<crypto>_balance_<player>%`
- `%cryptocurrency_balance_fiat%`
- `%cryptocurrency_balance_fiat_<player>%`
- `%cryptocurrency_<crypto>_price%`
- `%cryptocurrency_<crypto>_price_<amount>%`
- `%cryptocurrency_<crypto>_supply%`
- `%cryptocurrency_<crypto>_marketCap%`
- `%cryptocurrency_<crypto>_fast_change%`
- `%cryptocurrency_<crypto>_fast_change_fiat%`
- `%cryptocurrency_<crypto>_fast_change_percent%`
- `%cryptocurrency_<crypto>_fast_change_percent_abs%`
- `%cryptocurrency_<crypto>_fast_change_close%`
- `%cryptocurrency_<crypto>_fast_change_open%`
- `%cryptocurrency_<crypto>_fast_change_low%`
- `%cryptocurrency_<crypto>_fast_change_high%`
- `%cryptocurrency_<crypto>_daily_change%`
- `%cryptocurrency_<crypto>_daily_change_fiat%`
- `%cryptocurrency_<crypto>_daily_change_percent%`
- `%cryptocurrency_<crypto>_daily_change_percent_abs%`
- `%cryptocurrency_<crypto>_daily_change_close%`
- `%cryptocurrency_<crypto>_daily_change_open%`
- `%cryptocurrency_<crypto>_daily_change_low%`
- `%cryptocurrency_<crypto>_daily_change_high%`
- `%cryptocurrency_<crypto>_weekly_change%`
- `%cryptocurrency_<crypto>_weekly_change_fiat%`
- `%cryptocurrency_<crypto>_weekly_change_percent%`
- `%cryptocurrency_<crypto>_weekly_change_percent_abs%`
- `%cryptocurrency_<crypto>_weekly_change_close%`
- `%cryptocurrency_<crypto>_weekly_change_open%`
- `%cryptocurrency_<crypto>_weekly_change_low%`
- `%cryptocurrency_<crypto>_weekly_change_high%`
- `%cryptocurrency_<crypto>_monthly_change%`
- `%cryptocurrency_<crypto>_monthly_change_fiat%`
- `%cryptocurrency_<crypto>_monthly_change_percent%`
- `%cryptocurrency_<crypto>_monthly_change_percent_abs%`
- `%cryptocurrency_<crypto>_monthly_change_close%`
- `%cryptocurrency_<crypto>_monthly_change_open%`
- `%cryptocurrency_<crypto>_monthly_change_low%`
- `%cryptocurrency_<crypto>_monthly_change_high%`
