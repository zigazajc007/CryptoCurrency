<div align="center">
  <img src="https://cdn.rabbit-company.com/plugins/CryptoCurrency/banner.jpg" alt="CryptoCurrency banner">
</div>

<h3>Plugin Description:</h3>
<p>CryptoCurrency is a groundbreaking plugin for Minecraft that introduces the exciting world of cryptocurrencies to your gameplay experience. With CryptoCurrency plugin, players can seamlessly interact with real-world digital currencies within the Minecraft universe, adding a new layer of depth and engagement to your server.</p>

<h3>Features:</h3>
<ul>
	<li><p><b>Real-time Crypto Prices</b>: Plugin fetches up-to-date prices from popular cryptocurrency exchanges like Coinbase and Binance. Players can access accurate, real-time prices of their favorite cryptocurrencies directly within the game.</p></li>
	<li><p><b>In-Game Trading</b>: Through integration with the Vault plugin, CryptoCurrency allows players to buy, sell, and send cryptocurrencies to other players, enabling a dynamic in-game economy driven by digital currencies. Whether you're trading with friends or establishing your own virtual marketplace, plugin empowers players with the tools to engage in secure crypto transactions.</p></li>
	<li><p><b>Sign Shop Support</b>: Take your in-game economy to the next level with CryptoCurrency's support for sign shops. Players can create shops that accept cryptocurrencies as payment, allowing them to buy and sell in-game items using their virtual crypto wallets. This opens up new possibilities for player-driven economies and fosters a thriving marketplace within your Minecraft world.</p></li>
	<li><p><b>Mining Rewards</b>: Plugin introduces a unique mining system where players are rewarded with specific amounts of cryptocurrencies when they break blocks such as diamond ore, gold ore, iron ore... This creates an immersive and rewarding experience as players uncover valuable resources while earning digital assets.</p></li>
	<li><p><b>Configurability</b>: The CryptoCurrency plugin offers extensive configuration options, giving server administrators full control over the plugin's settings. You can customize supported cryptocurrencies, exchange sources, mining rewards, and various other aspects to tailor the gameplay experience to your server's needs.</p></li>
	<li><p><b>MySQL Database Support</b>: With MySQL database integration, you can connect multiple Minecraft servers and share player balances across them. This allows for a seamless experience when players move between different server instances, ensuring their cryptocurrency holdings remain consistent and synchronized.</p></li>
</ul>

<p>Step into the world of cryptocurrencies and revolutionize your Minecraft server with CryptoCurrency plugin. Embrace the future of digital assets, real-time prices, and an immersive in-game economy that empowers players to explore the potential of virtual currencies like never before.</p>

<p style="color: yellow">Note: Plugin does not involve real-world financial transactions or actual ownership of cryptocurrencies. It is solely a Minecraft plugin that simulates and integrates with virtual representations of digital currencies for in-game use.</p>

<h3>Default cryptocurrencies:</h3>
<ul>
	<li>btc (Bitcoin)
	<li>bch (Bitcoin cash)
	<li>eth (Ethereum)
	<li>etc (Ethereum classic)
	<li>doge (Dogecoin)
	<li>ltc (Litecoin)
	<li>usdt (Tether)
</ul>
<p>More cryptocurrencies can be added in cryptocurrencies.yml file.</p>

<h3>Commands:</h3>
<ul>
	<li>/&lt;crypto_name&gt; help
	<li>/&lt;crypto_name&gt; price
	<li>/&lt;crypto_name&gt; price &lt;amount&gt;
	<li>/&lt;crypto_name&gt; balance
	<li>/&lt;crypto_name&gt; balance &lt;player_name&gt;
	<li>/&lt;crypto_name&gt; buy &lt;amount&gt;
	<li>/&lt;crypto_name&gt; sell &lt;amount&gt;
	<li>/&lt;crypto_name&gt; send &lt;player&gt; &lt;amount&gt;
	<li>/&lt;crypto_name&gt; give &lt;player&gt; &lt;amount&gt;
	<li>/&lt;crypto_name&gt; take &lt;player&gt; &lt;amount&gt;
	<li>/&lt;crypto_name&gt; reload
</ul>

<h3>Permissions:</h3>
<ul>
	<li>cryptocurrency.reload
	<li>cryptocurrency.give
	<li>cryptocurrency.take
	<li>cryptocurrency.balance
	<li>cryptocurrency.shop
	<li>cryptocurrency.shop.break
</ul>

<h3>Placeholders:</h3>
<ul>
	<li>%cryptocurrency_&lt;crypto&gt;_balance%
	<li>%cryptocurrency_&lt;crypto&gt;_balance_&lt;player&gt;%
	<li>%cryptocurrency_balance_fiat%
	<li>%cryptocurrency_balance_fiat_&lt;player&gt;%
	<li>%cryptocurrency_&lt;crypto&gt;_price%
	<li>%cryptocurrency_&lt;crypto&gt;_price_&lt;amount&gt;%
	<li>%cryptocurrency_&lt;crypto&gt;_supply%
	<li>%cryptocurrency_&lt;crypto&gt;_marketCap%
	<li>%cryptocurrency_&lt;crypto&gt;_fast_change%
	<li>%cryptocurrency_&lt;crypto&gt;_fast_change_fiat%
	<li>%cryptocurrency_&lt;crypto&gt;_fast_change_percent%
	<li>%cryptocurrency_&lt;crypto&gt;_fast_change_percent_abs%
	<li>%cryptocurrency_&lt;crypto&gt;_fast_change_close%
	<li>%cryptocurrency_&lt;crypto&gt;_fast_change_open%
	<li>%cryptocurrency_&lt;crypto&gt;_fast_change_low%
	<li>%cryptocurrency_&lt;crypto&gt;_fast_change_high%
	<li>%cryptocurrency_&lt;crypto&gt;_daily_change%
	<li>%cryptocurrency_&lt;crypto&gt;_daily_change_fiat%
	<li>%cryptocurrency_&lt;crypto&gt;_daily_change_percent%
	<li>%cryptocurrency_&lt;crypto&gt;_daily_change_percent_abs%
	<li>%cryptocurrency_&lt;crypto&gt;_daily_change_close%
	<li>%cryptocurrency_&lt;crypto&gt;_daily_change_open%
	<li>%cryptocurrency_&lt;crypto&gt;_daily_change_low%
	<li>%cryptocurrency_&lt;crypto&gt;_daily_change_high%
	<li>%cryptocurrency_&lt;crypto&gt;_weekly_change%
	<li>%cryptocurrency_&lt;crypto&gt;_weekly_change_fiat%
	<li>%cryptocurrency_&lt;crypto&gt;_weekly_change_percent%
	<li>%cryptocurrency_&lt;crypto&gt;_weekly_change_percent_abs%
	<li>%cryptocurrency_&lt;crypto&gt;_weekly_change_close%
	<li>%cryptocurrency_&lt;crypto&gt;_weekly_change_open%
	<li>%cryptocurrency_&lt;crypto&gt;_weekly_change_low%
	<li>%cryptocurrency_&lt;crypto&gt;_weekly_change_high%
	<li>%cryptocurrency_&lt;crypto&gt;_monthly_change%
	<li>%cryptocurrency_&lt;crypto&gt;_monthly_change_fiat%
	<li>%cryptocurrency_&lt;crypto&gt;_monthly_change_percent%
	<li>%cryptocurrency_&lt;crypto&gt;_monthly_change_percent_abs%
	<li>%cryptocurrency_&lt;crypto&gt;_monthly_change_close%
	<li>%cryptocurrency_&lt;crypto&gt;_monthly_change_open%
	<li>%cryptocurrency_&lt;crypto&gt;_monthly_change_low%
	<li>%cryptocurrency_&lt;crypto&gt;_monthly_change_high%
</ul>