package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AppleScraper {

    // チェックするURL
    private static final String URL =
            "https://www.apple.com/jp/shop/refurbished/mac";

    // 探す機種名
    private static final String KEYWORD_MODEL = "MacBook Air";

    // 対象チップ（どれか1つ含まれていたらOK）
    private static final List<String> TARGET_CHIPS =
            List.of("M3", "M4", "M5");

    public static void main(String[] args) {

        // ① 設定ファイルを読み込む
        Properties config = loadConfig();
        if (config == null) return;

        String lineToken  = config.getProperty("LINE_ACCESS_TOKEN");
        String lineUserId = config.getProperty("LINE_USER_ID");

        // 日時を表示
        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        System.out.println("==========================================");
        System.out.println("チェック日時 : " + now);
        System.out.println("対象        : MacBook Air（M3 / M4 / M5）");
        System.out.println("==========================================\n");

        // ② 商品一覧を取得
        List<Product> products = fetchProducts();
        if (products == null) {
            System.out.println("❌ ページの取得に失敗しました。");
            return;
        }

        // ③ 全商品を表示
        System.out.println("📋 現在の掲載商品（" + products.size() + "件）:");
        for (Product p : products) {
            System.out.println("   - " + p.name);
        }

        // ④ MacBook Air M3/M4/M5 を検索
        System.out.println("\n🔍 MacBook Air（M3/M4/M5）を検索中...\n");

        List<Product> matched = new ArrayList<>();
        for (Product p : products) {
            boolean isAir = p.name.contains(KEYWORD_MODEL);
            boolean hasChip = TARGET_CHIPS.stream()
                    .anyMatch(chip -> p.name.contains(chip));
            if (isAir && hasChip) {
                matched.add(p);
            }
        }

        // ⑤ 結果を出力
        if (matched.isEmpty()) {
            System.out.println("😴 MacBook Air（M3/M4/M5）はまだ掲載されていません。");
        } else {
            System.out.println("🎉🎉 見つかりました！！ 🎉🎉");
            for (Product p : matched) {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("商品名 : " + p.name);
                System.out.println("URL    : " + p.url);
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━");
            }
            // ⑥ LINEに通知を送る
            sendLineNotification(matched, lineToken, lineUserId);
        }
    }

    // ---------------------------------------------------
    // config.properties を読み込むメソッド
    // ---------------------------------------------------
    private static Properties loadConfig() {
        Properties cfg = new Properties();

        // GitHub Actions用：環境変数から読み込む
        String token = System.getenv("LINE_ACCESS_TOKEN");
        String user  = System.getenv("LINE_USER_ID");

        if (token != null && user != null) {
            cfg.setProperty("LINE_ACCESS_TOKEN", token);
            cfg.setProperty("LINE_USER_ID", user);
            System.out.println("✅ 環境変数から設定を読み込みました");
            return cfg;
        }

        // ローカル用：config.propertiesから読み込む
        try (FileInputStream in = new FileInputStream(
                "src/main/resources/config.properties")) {
            cfg.load(in);
            System.out.println("✅ config.propertiesから設定を読み込みました");
        } catch (IOException e) {
            System.out.println("❌ 設定ファイルが見つかりません: " + e.getMessage());
        }
        return cfg;
    }
s

    // ---------------------------------------------------
    // Apple整備済みページから商品一覧を取得するメソッド
    // ---------------------------------------------------
    private static List<Product> fetchProducts() {
        try {
            System.out.println("🌐 ページ取得中...\n");

            Document doc = Jsoup.connect(URL)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
                            + "AppleWebKit/537.36 (KHTML, like Gecko) "
                            + "Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10_000)
                    .get();

            Elements links = doc.select("h3 a");

            List<Product> products = new ArrayList<>();
            for (Element link : links) {
                String name = link.text();
                String url  = link.attr("abs:href");
                if (!name.isEmpty()) {
                    products.add(new Product(name, url));
                }
            }
            return products;

        } catch (IOException e) {
            System.out.println("エラー: " + e.getMessage());
            return null;
        }
    }

    // ---------------------------------------------------
    // LINE Messaging API で通知を送るメソッド
    // ---------------------------------------------------
    private static void sendLineNotification(
            List<Product> products, String token, String userId) {

        // メッセージを組み立てる
        StringBuilder sb = new StringBuilder();
        sb.append("🚨 MacBook Air が整備済み品に入荷！\\n\\n");
        for (Product p : products) {
            sb.append("📦 ").append(p.name).append("\\n");
            sb.append("🔗 ").append(p.url).append("\\n\\n");
        }
        sb.append("👆 今すぐチェック！");

        // JSONを組み立てる
        String json = "{"
                + "\"to\": \"" + userId + "\","
                + "\"messages\": [{"
                + "\"type\": \"text\","
                + "\"text\": \"" + sb + "\""
                + "}]}";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.line.me/v2/bot/message/push"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("✅ LINE通知を送信しました！");
            } else {
                System.out.println("⚠️ LINE送信エラー: " + response.statusCode());
                System.out.println("   詳細: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("❌ 通知エラー: " + e.getMessage());
        }
    }

    // ---------------------------------------------------
    // 商品情報を入れる入れ物
    // ---------------------------------------------------
    static class Product {
        String name;
        String url;

        Product(String name, String url) {
            this.name = name;
            this.url  = url;
        }
    }
}
