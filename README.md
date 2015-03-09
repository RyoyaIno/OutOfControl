# OutOfControl
Windows用Javaアプレットで作成した2Dアクションゲーム
(macではうまく動作しない)

[注意事項]
ブラウザに組み込んで使うことを前提としているのでJavaScriptとの非同期通信のLiveConnectを使用している。
そのままeclipseなどでデバッグするときは、LiveConnect部分をコメントアウトし、実行構成のパラメーターを適切に送る。
例:
  best_time:100000
  clear_times:10
  id:Ryoya
  play_times:50
  saveFlag:0
  
[反省点]
MainPanelにコーディングが集中しすぎてしまったので、しっかりと全体構造プランを立ててデータを扱うクラスを分散すべきであった。
