# 🍎 Apple整備済み品 MacBook Air 通知アプリ

![Java](https://img.shields.io/badge/Java-21-orange)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-自動実行-blue)
![LINE](https://img.shields.io/badge/LINE-通知対応-green)
![Status](https://img.shields.io/badge/Status-稼働中-brightgreen)

---

## 🎯 なぜ作ったか

きっかけは元エンジニアの友達から話を聞き興味をもったことです。

私は今、2017年モデルのMacBookを使い続けていて、そろそろ新しいMacBookが必要だと思いました。
Appleには **"整備済み品"** という、新品より安く買える公式の中古商品がありますが、
社会人の自分は毎時間MacBookが売られていないかチェックできないので
**「入荷したら自動でLINEに通知してくれるアプリを作ろう！」** と思ったのがきっかけです。

しかもちょうどJavaの勉強を始めたばかりだったので、
**勉強しながら実用的なものを作ろう** かなって思ったんです。

---

## 📱 どんなアプリか

Apple公式の整備済み品ページを **"3時間ごとに自動監視"** して、
**"MacBook Air（M3/M4/M5)"** が入荷したら即座に **LINE** で通知します。

GitHubのサーバーが自動で動くので、**Macの電源を切っていても24時間監視**し続けてくれます。
本当にこれを知った時は感動しました。

---

## 🗺️ 全体の仕組み

<img width="768" height="1376" alt="CmDWWq5i" src="https://github.com/user-attachments/assets/9065f78d-a966-48d7-bc41-d9f72646a655" />

- **Mac**でコードを書いて `git push` でGitHubに送る
- **GitHub**がコードを保管する（ポートフォリオにもなる）
- **GitHub Actions**というロボットが3時間ごとに自動でコードを実行してくれる
- Macの電源を切っていても動き続ける 🚀

---

## 🔄 動作フロー（プログラムが動く流れ）

<img width="768" height="1376" alt="qY9STotg" src="https://github.com/user-attachments/assets/35c4255f-0a83-479a-8d0c-996d9837b7d5" />

1. **3時間ごとに起動** → Appleの整備済み品ページにアクセス
2. **JSoup**というツールでページのHTMLを丸ごと取得
3. 商品一覧から **「MacBook Air」+「M3/M4/M5」** を検索
4. 見つからない → 😴 何もしない・次の3時間後にまた確認
5. 見つかった → 📱 **LINEに通知が届く！**

---

## 📁 ファイル構成と各ファイルの役割

<img width="896" height="1200" alt="HU0ilXSO" src="https://github.com/user-attachments/assets/e6e58c7a-4393-4ac2-8a24-7067e341e5bd" />


```
apple-refurbished-notifier/
├── .github/                          # GitHubに特別な指示を渡すための隠しフォルダ（.ドットから始まる＝隠しファイル）
│   └── workflows/                    # 「自動実行の手順書」を入れるフォルダ
│       └── check.yml                 # 「3時間ごとにこのコードを動かして」というロボットへの指示書
│
├── src/                              # "source"の略。自分で書いたコードを入れるフォルダ
│   └── main/                         # 実際に動くメインのコードを入れる場所
│       ├── java/org/example/         # Javaのコードを入れるフォルダ（org/exampleはパッケージ名）
│       │   └── AppleScraper.java     # このアプリの頭脳。Appleのページを見に行き・探して・LINEに送る処理が全部ここに書いてある
│       └── resources/                # コードではないけど必要なファイル（設定ファイルなど）を入れる場所
│           └── config.properties     # LINEのアクセストークンとユーザーIDを保存するファイル。絶対にGitHubに上げてはいけない秘密情報
│
├── .gitignore                        # 「このファイルはGitHubに上げないで」というリスト。config.propertiesやtarget/などを指定している
└── pom.xml                           #  このプロジェクトで使う外部ツール（JSoupなど）を注文するリスト。Mavenというツールがここを読んで自動でダウンロードしてくれる

---

## GitとGitHubの違い

```
【Git】= Macの中にある「変更履歴の日記帳」

  git add .            → 今日の変更をメモする
  git commit -m "説明" → 日記に書き込む
  git push             → 日記をクラウドに送る

↓pushすると

【GitHub】= インターネット上にある「コードの保管庫」

  ・コードをクラウドに保存できる
  ・世界中の人に見せられる（ポートフォリオになる）
  ・GitHub Actionsという自動実行機能が使える

## Github Actionとは

check.yml = ロボットへの指示書

「毎日0時・3時・6時・9時・12時・15時・18時・21時（UTC）に
 Ubuntuサーバーを起動して
 Javaをインストールして
 AppleScraper.javaを実行してください」

→ Macの電源がOFFでも動き続ける。
```

## 🛠️ 使用技術

| 技術 | バージョン | 用途 |
|------|-----------|------|
| Java | 21 | メインのプログラミング言語 |
| JSoup | 1.17.2 | WebページのHTMLを取得・解析 |

Java勉強中の社会人 / 2026年3月 初めてのアプリ完成 🎉  
2017年モデルのMacBookで開発しました 💻
