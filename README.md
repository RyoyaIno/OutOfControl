# OutOfControl
Javaアプレットで作成した2Dアクションゲーム<br>
EclipseではWindowsのみデバッグ可能(Mac不可)<br>
ブラウザ上ではWindowsもMacも動くがMacは矢印キー入力が効かず、「I」,「J」,「K」,「L」キーで操作することになる上に長押しの反応が悪いのでWindowsでのプレイ推奨。<br>
<br>
[注意事項]<br>
ブラウザに組み込んで使うことを前提としているのでJavaScriptとの非同期通信のLiveConnectを使用している。<br>
そのままeclipseなどでデバッグするときは、LiveConnect部分をコメントアウトし、実行構成のパラメーターを適切に送る。<.br
例:<br>
  best_time:100000<br>
  clear_times:10<br>
  id:Ryoya<br>
  play_times:50<br>
  saveFlag:0<br>
<br>
[反省点]<br>
MainPanelにコーディングが集中しすぎてしまったので、しっかりと全体構造プランを立ててデータを扱うクラスを分散すべきであった。<br>
<br>
[参考サイト]<br>
開発：<a href="http://aidiary.hatenablog.com/entry/20040918/1251373370">Javaでゲームつくりますが何か？</a><br>
効果音：<a href="http://soundeffect-lab.info">効果音ラボ</a><br>
画像：<a href="http://wato5576.sukimakaze.com">すきまの素材</a><br>
BGM：<a href="http://regist-band.cross-horizon.com">ErogenousRockets</a>(自作)<br>
