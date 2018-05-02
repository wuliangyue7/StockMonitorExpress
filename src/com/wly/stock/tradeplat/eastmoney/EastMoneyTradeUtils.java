package com.wly.stock.tradeplat.eastmoney;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.network.http.CookieItem;
import com.wly.stock.common.OrderInfo;
import com.wly.stock.common.StockConst;
import com.wly.stock.common.TradeInfo;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuly on 2018/3/13.
 */
public class EastMoneyTradeUtils
{
    static public final String RootUrl = "https://jy.xzsec.com";
    static public final String OrderUrl = "/Trade/SubmitTrade?validatekey=";
    static public void DoTrade(String acct, String code, String name, int tradeFlag, int count, float price)
    {
        String loginInfo = Utils.ReadFile(String.format("loginInfo/%s.txt", acct));
        JsonObject jsonObject = new JsonParser().parse(loginInfo).getAsJsonObject();
        CookieStore  cookieStore = new BasicCookieStore();
        CookieItem.FillCookieStore(cookieStore, jsonObject.getAsJsonArray("cookies"));
        String validatekey = jsonObject.get("validatekey").getAsString();

        TradeInfo  tradeInfo = new TradeInfo();
        tradeInfo.code = code;
        tradeInfo.name = name;
        tradeInfo.orderCount = count;
        tradeInfo.tradeFlag = tradeFlag;
        tradeInfo.orderPrice = price;
        DoTrade(cookieStore, validatekey, tradeInfo);
    }

    static public void DoTrade(CookieStore cookieStore, String validatekey, TradeInfo trandeInfo)
    {
        HttpPost httpPost = new HttpPost(RootUrl + OrderUrl+validatekey);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("stockCode", trandeInfo.code));
        params.add(new BasicNameValuePair("price", String.format("%.2f",trandeInfo.orderPrice)));
        params.add(new BasicNameValuePair("amount", Integer.toString(trandeInfo.orderCount)));
        params.add(new BasicNameValuePair("tradeType", trandeInfo.tradeFlag== StockConst.TradeBuy?"B":"S"));
        params.add(new BasicNameValuePair("zqmc", trandeInfo.name));
        try
        {
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpClientContext localContext = new HttpClientContext();
            localContext.setCookieStore(cookieStore);
            CloseableHttpResponse response = httpclient.execute(httpPost, localContext);
            String retStr = Utils.GetResponseContent(response);
            System.out.println("orderResponse: " + retStr);
            //{"Message":null,"Status":0,"Data":[{"Wtbh":"324917"}]}
            JsonObject jsonObject = new JsonParser().parse(retStr).getAsJsonObject();
            int stat = jsonObject.get("Status").getAsInt();
            if(stat != 0)
            {
                System.out.println("DoOrder failed! "+jsonObject.get("Message").getAsString());
                return;
            }
            else
            {
                System.out.println("DoOrder Succ! ");
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    static public void QueryMoneyInfo(CookieStore cookieStore, String validatekey)
    {
        try
        {
            final String GetRmbAssetPage = "/Com/GetAssets?validatekey=";
            HttpPost httpPost = new HttpPost(RootUrl + GetRmbAssetPage+validatekey);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("moneyType", "RMB"));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpClientContext localContext = new HttpClientContext();
            localContext.setCookieStore(cookieStore);
            CloseableHttpResponse response = httpclient.execute(httpPost, localContext);

            String retStr = Utils.GetResponseContent(response);
            //{"Message":null,"Status":0,"Data":[{"RMBZzc":"1.73","Zzc":"1.73","Zxsz":"0.73","Kyzj":"1.73","Kqzj":"1.00","Djzj":"0.00","Zjye":"1.00","Money_type":"RMB","Drckyk":null,"Ljyk":null}]}
            System.out.println(retStr);
            JsonObject jsonObject = new JsonParser().parse(retStr).getAsJsonObject();
            int stat = jsonObject.get("Status").getAsInt();
            if(stat != 0)
            {
                System.out.println("get asset failed! "+jsonObject.get("Message").getAsString());
                return;
            }
            JsonArray jsonDataArray = jsonObject.get("Data").getAsJsonArray();
            LogUtils.LogRealtime(String.format("money: %.2f", jsonDataArray.get(0).getAsJsonObject().get("Kyzj").getAsFloat()));
            //            return  jsonDataArray.get(0).getAsJsonObject().get("Kyzj").getAsFloat();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    static public void QueryTradeInfo(CookieStore cookieStore, String validatekey)
    {
        try
        {
            final String QueryUrl = "/Search/GetOrdersData?validatekey=";
            HttpPost httpPost = new HttpPost(RootUrl + QueryUrl + validatekey);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("qqhs", "20"));
            params.add(new BasicNameValuePair("wc", ""));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpClientContext localContext = new HttpClientContext();
            localContext.setCookieStore(cookieStore);
            CloseableHttpResponse response = httpclient.execute(httpPost, localContext);

            String retStr = Utils.GetResponseContent(response);
             System.out.println(retStr);

            JsonObject jsonObject = new JsonParser().parse(retStr).getAsJsonObject();
            int stat = jsonObject.get("Status").getAsInt();
            if (stat != 0)
            {
                System.out.println("QueryTradeInfo failed! " + jsonObject.get("Message").getAsString());
                return;
            }

            JsonArray jsonDataArray = jsonObject.get("Data").getAsJsonArray();
            int i;
            JsonObject newOrderInfo;
            String tradeId;
            String code;
            String name;
            String tradeDesc;
            String totalCount;
            String priceOrder;
            String tradeStat;
            String dealCount;
            String priceDeal;

            String strTmp;
            for (i = 0; i < jsonDataArray.size(); ++i)
            {
                newOrderInfo = jsonDataArray.get(i).getAsJsonObject();
                tradeId = newOrderInfo.get("Wtbh").getAsString();
                code = newOrderInfo.get("Zqdm").getAsString();
                name = newOrderInfo.get("Zqmc").getAsString();
                tradeDesc = newOrderInfo.get("Mmsm").getAsString();
                totalCount = newOrderInfo.get("Wtsl").getAsString();
                priceOrder = newOrderInfo.get("Wtjg").getAsString();
                tradeStat = newOrderInfo.get("Wtzt").getAsString();
                dealCount = newOrderInfo.get("Cjsl").getAsString();
                priceDeal = newOrderInfo.get("Cjje").getAsString();

                strTmp = String.format("%s %s %s %s %s %s %s %s %s %s", tradeId, code, name, tradeDesc, totalCount,
                        priceOrder, tradeStat, dealCount, priceDeal);
                LogUtils.LogRealtime(strTmp);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
