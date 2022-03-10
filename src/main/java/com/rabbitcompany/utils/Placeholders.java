package com.rabbitcompany.utils;

import com.rabbitcompany.CryptoCurrency;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class Placeholders extends PlaceholderExpansion {

    @Override
    public boolean canRegister(){ return true; }

    @Override
    public @NotNull String getIdentifier() {
        return "cryptocurrency";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Black1_TV";
    }

    @Override
    public @NotNull String getVersion() {
        return "3.2.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {

        String[] identi = identifier.split("_");
        //%cryptocurrency_balance_fiat_<player>%
        if(identi.length == 3 && identi[0].equals("balance") && identi[1].equals("fiat")) return API.moneyFormatter.format(API.getTotalAssets(identi[2]));

        //%cryptocurrency_balance_fiat%
        if(identi.length == 2 && identi[0].equals("balance") && identi[1].equals("fiat")){
            if (offlinePlayer == null) return "";
            if (offlinePlayer.getPlayer() == null) return "";
            return API.moneyFormatter.format(API.getTotalAssets(offlinePlayer.getPlayer().getName()));
        }

        if(Settings.cryptos.get(identi[0]) == null) return "";
        //%cryptocurrency_<crypto>_price_<amount>%
        if(identi.length == 3 && identi[1].equals("price") && Number.isNumeric(identi[2])) return API.getCryptoPriceFormatted(identi[0], Double.parseDouble(identi[2]));
        //%cryptocurrency_<crypto>_balance_<player>%
        if(identi.length == 3 && identi[1].equals("balance") && !identi[2].equals("fiat")) return API.getBalanceFormatted(identi[2], identi[0]);
        //%cryptocurrency_<crypto>_balance_fiat_<player>%
        if(identi.length == 4 && identi[1].equals("balance") && identi[2].equals("fiat")) return API.moneyFormatter.format(API.getCryptoPrice(identi[0], API.getBalance(identi[3], identi[0])));
        //%cryptocurrency_<crypto>_supply%
        if(identi.length == 2 && identi[1].equals("supply")) return API.getFormatter(identi[0]).format(API.getCryptoSupply(identi[0]));
        //%cryptocurrency_<crypto>_maxSupply%
        if(identi.length == 2 && identi[1].equals("maxSupply")) return API.getFormatter(identi[0]).format(API.getCryptoMaxSupply(identi[0]));
        //%cryptocurrency_<crypto>_marketCap%
        if(identi.length == 2 && identi[1].equals("marketCap")) return API.moneyFormatter.format(API.getCryptoMarketCap(identi[0]));
        //%cryptocurrency_<crypto>_price%
        if(identi.length == 2 && identi[1].equals("price")) return API.getCryptoPriceFormatted(identi[0]);

        //
        //  Fast Change
        //

        //%cryptocurrency_<crypto>_fast_change%
        if(identi.length == 3 && identi[1].equals("fast") && identi[2].equals("change")){
            return (Settings.cryptos.get(identi[0]).price >= Settings.cryptos.get(identi[0]).previousPrice) ? CryptoCurrency.getInstance().getConf().getString("price_rising", "&a▲") : CryptoCurrency.getInstance().getConf().getString("price_falling", "&c▼");
        }
        //%cryptocurrency_<crypto>_fast_change_fiat%
        if(identi.length == 4 && identi[1].equals("fast") && identi[2].equals("change") && identi[3].equals("fiat")){
            double previousCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now() + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return API.moneyFormatter.format(cryptoPrice - previousCryptoPrice);
        }
        //%cryptocurrency_<crypto>_fast_change_percent%
        if(identi.length == 4 && identi[1].equals("fast") && identi[2].equals("change") && identi[3].equals("percent")) {
            double previousCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now() + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            double percent = Number.roundDouble(Math.abs(previousCryptoPrice - cryptoPrice) / ((previousCryptoPrice + cryptoPrice) / 2.0) * 100, 2);
            return (previousCryptoPrice - cryptoPrice < 0) ? CryptoCurrency.getInstance().getConf().getString("percentage_rising", "&a+") + percent : CryptoCurrency.getInstance().getConf().getString("percentage_falling", "&c-") + percent;
        }
        //%cryptocurrency_<crypto>_fast_change_percent_abs%
        if(identi.length == 5 && identi[1].equals("fast") && identi[2].equals("change") && identi[3].equals("percent") && identi[4].equals("abs")) {
            double previousCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now() + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return "" + Number.roundDouble(Math.abs(previousCryptoPrice - cryptoPrice) / ((previousCryptoPrice + cryptoPrice) / 2.0) * 100, 2);
        }
        //%cryptocurrency_<crypto>_fast_change_high%
        if(identi.length == 4 && identi[1].equals("fast") && identi[2].equals("change") && identi[3].equals("high")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now() + ".max", 0));
        }
        //%cryptocurrency_<crypto>_fast_change_low%
        if(identi.length == 4 && identi[1].equals("fast") && identi[2].equals("change") && identi[3].equals("low")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now() + ".min", 0));
        }
        //%cryptocurrency_<crypto>_fast_change_open%
        if(identi.length == 4 && identi[1].equals("fast") && identi[2].equals("change") && identi[3].equals("open")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now() + ".open", 0));
        }
        //%cryptocurrency_<crypto>_fast_change_close%
        if(identi.length == 4 && identi[1].equals("fast") && identi[2].equals("change") && identi[3].equals("close")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now() + ".close", 0));
        }

        //
        //  Daily Change
        //

        //%cryptocurrency_<crypto>_daily_change%
        if(identi.length == 3 && identi[1].equals("daily") && identi[2].equals("change")){
            double yesterdayCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return (cryptoPrice >= yesterdayCryptoPrice) ? CryptoCurrency.getInstance().getConf().getString("price_rising", "&a▲") : CryptoCurrency.getInstance().getConf().getString("price_falling", "&c▼");
        }
        //%cryptocurrency_<crypto>_daily_change_fiat%
        if(identi.length == 4 && identi[1].equals("daily") && identi[2].equals("change") && identi[3].equals("fiat")){
            double yesterdayCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return API.moneyFormatter.format(cryptoPrice - yesterdayCryptoPrice);
        }
        //%cryptocurrency_<crypto>_daily_change_percent%
        if(identi.length == 4 && identi[1].equals("daily") && identi[2].equals("change") && identi[3].equals("percent")) {
            double yesterdayCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            double percent = Number.roundDouble(Math.abs(yesterdayCryptoPrice - cryptoPrice) / ((yesterdayCryptoPrice + cryptoPrice) / 2.0) * 100, 2);
            return (yesterdayCryptoPrice - cryptoPrice < 0) ? CryptoCurrency.getInstance().getConf().getString("percentage_rising", "&a+") + percent : CryptoCurrency.getInstance().getConf().getString("percentage_falling", "&c-") + percent;
        }
        //%cryptocurrency_<crypto>_daily_change_percent_abs%
        if(identi.length == 5 && identi[1].equals("daily") && identi[2].equals("change") && identi[3].equals("percent") && identi[4].equals("abs")) {
            double dailyCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now() + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return "" + Number.roundDouble(Math.abs(dailyCryptoPrice - cryptoPrice) / ((dailyCryptoPrice + cryptoPrice) / 2.0) * 100, 2);
        }
        //%cryptocurrency_<crypto>_daily_change_high%
        if(identi.length == 4 && identi[1].equals("daily") && identi[2].equals("change") && identi[3].equals("high")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".max", 0));
        }
        //%cryptocurrency_<crypto>_daily_change_low%
        if(identi.length == 4 && identi[1].equals("daily") && identi[2].equals("change") && identi[3].equals("low")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".min", 0));
        }
        //%cryptocurrency_<crypto>_daily_change_open%
        if(identi.length == 4 && identi[1].equals("daily") && identi[2].equals("change") && identi[3].equals("open")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".open", 0));
        }
        //%cryptocurrency_<crypto>_daily_change_close%
        if(identi.length == 4 && identi[1].equals("daily") && identi[2].equals("change") && identi[3].equals("close")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".close", 0));
        }

        //
        //  Weekly Change
        //

        //%cryptocurrency_<crypto>_weekly_change%
        if(identi.length == 3 && identi[1].equals("weekly") && identi[2].equals("change")){
            double weeklyCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusWeeks(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return (cryptoPrice >= weeklyCryptoPrice) ? CryptoCurrency.getInstance().getConf().getString("price_rising", "&a▲") : CryptoCurrency.getInstance().getConf().getString("price_falling", "&c▼");
        }
        //%cryptocurrency_<crypto>_weekly_change_fiat%
        if(identi.length == 4 && identi[1].equals("weekly") && identi[2].equals("change") && identi[3].equals("fiat")){
            double weeklyCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusWeeks(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return API.moneyFormatter.format(cryptoPrice - weeklyCryptoPrice);
        }
        //%cryptocurrency_<crypto>_weekly_change_percent%
        if(identi.length == 4 && identi[1].equals("weekly") && identi[2].equals("change") && identi[3].equals("percent")) {
            double weeklyCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusWeeks(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            double percent = Number.roundDouble(Math.abs(weeklyCryptoPrice - cryptoPrice) / ((weeklyCryptoPrice + cryptoPrice) / 2.0) * 100, 2);
            return (weeklyCryptoPrice - cryptoPrice < 0) ? CryptoCurrency.getInstance().getConf().getString("percentage_rising", "&a+") + percent : CryptoCurrency.getInstance().getConf().getString("percentage_falling", "&c-") + percent;
        }
        //%cryptocurrency_<crypto>_weekly_change_percent_abs%
        if(identi.length == 5 && identi[1].equals("weekly") && identi[2].equals("change") && identi[3].equals("percent") && identi[4].equals("abs")) {
            double weeklyCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now() + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return "" + Number.roundDouble(Math.abs(weeklyCryptoPrice - cryptoPrice) / ((weeklyCryptoPrice + cryptoPrice) / 2.0) * 100, 2);
        }
        //%cryptocurrency_<crypto>_weekly_change_high%
        if(identi.length == 4 && identi[1].equals("weekly") && identi[2].equals("change") && identi[3].equals("high")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusWeeks(1) + ".max", 0));
        }
        //%cryptocurrency_<crypto>_weekly_change_low%
        if(identi.length == 4 && identi[1].equals("weekly") && identi[2].equals("change") && identi[3].equals("low")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusWeeks(1) + ".min", 0));
        }
        //%cryptocurrency_<crypto>_weekly_change_open%
        if(identi.length == 4 && identi[1].equals("weekly") && identi[2].equals("change") && identi[3].equals("open")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusWeeks(1) + ".open", 0));
        }
        //%cryptocurrency_<crypto>_weekly_change_close%
        if(identi.length == 4 && identi[1].equals("weekly") && identi[2].equals("change") && identi[3].equals("close")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusWeeks(1) + ".close", 0));
        }

        //
        //  Monthly Change
        //

        //%cryptocurrency_<crypto>_monthly_change%
        if(identi.length == 3 && identi[1].equals("monthly") && identi[2].equals("change")){
            double monthlyCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusMonths(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return (cryptoPrice >= monthlyCryptoPrice) ? CryptoCurrency.getInstance().getConf().getString("price_rising", "&a▲") : CryptoCurrency.getInstance().getConf().getString("price_falling", "&c▼");
        }
        //%cryptocurrency_<crypto>_monthly_change_fiat%
        if(identi.length == 4 && identi[1].equals("monthly") && identi[2].equals("change") && identi[3].equals("fiat")){
            double monthlyCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusMonths(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return API.moneyFormatter.format(cryptoPrice - monthlyCryptoPrice);
        }
        //%cryptocurrency_<crypto>_monthly_change_percent%
        if(identi.length == 4 && identi[1].equals("monthly") && identi[2].equals("change") && identi[3].equals("percent")) {
            double monthlyCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusMonths(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            double percent = Number.roundDouble(Math.abs(monthlyCryptoPrice - cryptoPrice) / ((monthlyCryptoPrice + cryptoPrice) / 2.0) * 100, 2);
            return (monthlyCryptoPrice - cryptoPrice < 0) ? CryptoCurrency.getInstance().getConf().getString("percentage_rising", "&a+") + percent : CryptoCurrency.getInstance().getConf().getString("percentage_falling", "&c-") + percent;
        }
        //%cryptocurrency_<crypto>_monthly_change_percent_abs%
        if(identi.length == 5 && identi[1].equals("monthly") && identi[2].equals("change") && identi[3].equals("percent") && identi[4].equals("abs")) {
            double monthlyCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now() + ".avg", Settings.cryptos.get(identi[0]).price);
            double cryptoPrice = Settings.cryptos.get(identi[0]).price;
            return "" + Number.roundDouble(Math.abs(monthlyCryptoPrice - cryptoPrice) / ((monthlyCryptoPrice + cryptoPrice) / 2.0) * 100, 2);
        }
        //%cryptocurrency_<crypto>_monthly_change_high%
        if(identi.length == 4 && identi[1].equals("monthly") && identi[2].equals("change") && identi[3].equals("high")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusMonths(1) + ".max", 0));
        }
        //%cryptocurrency_<crypto>_monthly_change_low%
        if(identi.length == 4 && identi[1].equals("monthly") && identi[2].equals("change") && identi[3].equals("low")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusMonths(1) + ".min", 0));
        }
        //%cryptocurrency_<crypto>_monthly_change_open%
        if(identi.length == 4 && identi[1].equals("monthly") && identi[2].equals("change") && identi[3].equals("open")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusMonths(1) + ".open", 0));
        }
        //%cryptocurrency_<crypto>_monthly_change_close%
        if(identi.length == 4 && identi[1].equals("monthly") && identi[2].equals("change") && identi[3].equals("close")){
            return API.getCryptoPriceFormatted(identi[0], Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusMonths(1) + ".close", 0));
        }

        if (offlinePlayer == null) return "";
        if (offlinePlayer.getPlayer() == null) return "";

        final Player player = offlinePlayer.getPlayer();

        //%cryptocurrency_<crypto>_balance%
        if(identi.length == 2 && identi[1].equals("balance")) return API.getBalanceFormatted(player.getName(), identi[0]);

        //%cryptocurrency_<crypto>_balance_fiat%
        if(identi.length == 3 && identi[1].equals("balance") && identi[2].equals("fiat")) return API.moneyFormatter.format(API.getCryptoPrice(identi[0], API.getBalance(player.getName(), identi[0])));

        //%cryptocurrency_<crypto>_change_fiat_player%
        if(identi.length == 4 && identi[1].equals("change") && identi[2].equals("fiat") && identi[3].equals("player")){
            double balance = API.getBalance(player.getName(), identi[0]);
            double yesterdayCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double yesterdayPrice = yesterdayCryptoPrice * balance;
            double price = API.getCryptoPrice(identi[0], balance);
            return API.moneyFormatter.format(price - yesterdayPrice);
        }
        //%cryptocurrency_<crypto>_change_percent_player%
        if(identi.length == 4 && identi[1].equals("change") && identi[2].equals("percent") && identi[3].equals("player")) {
            double balance = API.getBalance(player.getName(), identi[0]);
            double yesterdayCryptoPrice = Settings.cryptos.get(identi[0]).history.getDouble(LocalDate.now().minusDays(1) + ".avg", Settings.cryptos.get(identi[0]).price);
            double yesterdayPrice = yesterdayCryptoPrice * balance;
            double price = API.getCryptoPrice(identi[0], balance);
            return "" + Number.roundDouble(Math.abs(yesterdayPrice - price) / ((yesterdayCryptoPrice + price) / 2.0) * 100, 2);
        }
        return "";
    }
}
