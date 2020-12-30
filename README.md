# Omikuji
## 概要
おみくじを実装するPluginです。  
動作期待バージョン: 1.8 - 1.16.4
動作確認済みバージョン: 1.8.8, 1.12.2, 1.16.4

## 機能
* おみくじが引ける
* 結果を自由に編集できる
* 結果によってアイテムを与えることができる
* おみくじに料金を課せられる
* プレイヤーごとに引ける回数を指定できる
* コマンドと看板両方で引ける (設定で無効化することも可能)
* 設定時刻になったら自動的におみくじを解禁、禁止できる

## コマンド
|  コマンド  |  内容  | 権限 |
| :----: | :---- | :----: |
|/omikuji|おみくじを引きます|omikuji.allowdraw|
|/omikuji help|コマンド一覧を表示します|omikuji.admin|
|/omikuji reload|設定ファイルをリロードします|omikuji.admin|
|/omikuji info|おみくじの結果一覧を表示します|omikuji.admin|
|/omikuji viewItem <ID>|当たった時に貰えるアイテムを表示します|omikuji.admin|
|/omikuji setItem <ID>|当たった時に貰えるアイテムを設定します|omikuji.admin|

## 権限
|  権限  |  説明 | デフォルト |
| :----: | :---- | :----: |
|omikuji.allowdraw|おみくじを引くことができる権限| 全員 |
|omikuji.command.omikuji|/omikujiコマンドを実行できる権限|全員|
|omikuji.admin|/omikujiコマンドの運営用パラメーターを実行できる権限|OP|
|omikuji.bypass.drawtimelimit|AutoEnable/AutoDisableを設定中に、その制限を免除できる権限|OP|
|omikuji.bypass.cost|料金を設定している場合、それを免除できる権限|OP|
|omikuji.bypass.amount|引ける量を制限している場合、それを免除できる権限|OP|

## ライセンス (License)
[GNU General Public License v3.0](LICENSE)

## 製作者
* [siloneco](https://github.com/siloneco)
