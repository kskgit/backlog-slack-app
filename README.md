
# Slack-to-Backlog とは
Slack-to-Backlog は SlackのメッセージをBacklogの課題に登録出来るSlack appです

### 構成図&動作イメージ
https://nulab-exam.backlog.jp/alias/wiki/1025473

# 主なパッケージの役割分担
全体的にDIを用いてパッケージ間は疎結合となるよう工夫しました

### repository
##### 外部API通信用の処理を記載
- clientパッケージ配下のクラスを経由して外部通信を行う

#### client
##### クライアントライブラリをラップ
- clientライブラリ仕様変更の影響範囲を小さくする目的

### service
##### ビジネスロジックを記載
- Slackからのイベントを受け取り各repositoryへ処理を投げ、結果をチャットへ返す

### endpoint
##### slackのイベントを登録

# やっていないこと
- appの公開
  - 以下手順に従い配布可能な状態にしようと試みるも上手く反映されないため、添付の動画から動作を確認いただきたいです。
    - https://slack.dev/java-slack-sdk/guides/ja/app-distribution
    - `Slack SDK for Java`固有の問題を解決することよりも、コードのリファクタを優先して取り組みたかったため

- やる必要はあるが時間が足りなかった
  - TODOコメントで残しているもの
  - 全体的なエラーハンドリング

- 意図が伝わりづらそうな箇所は`NOTE:`でコメントしております