# oPEN

[OpenBlocks][openblocks] を用いた初学者向けプログラミング学習環境 oPEN のリポジトリです。

## 開発を行う手順
### 必要なソフトウェア

* [Eclipse][eclipse]
* [git][git]
* [Apache Maven][maven]

### リポジトリを取得

```
% cd ~/git/
% git clone https://github.com/xDNCL/oPEN.git
```

### OpenBlocks のパッケージ作成

```
% cd ~/git/oPEN/openblocks
% mvn clean package
```

### Eclipse へ oPEN のプロジェクトをインポート

1. Eclipseを起動
2. 「ファイル」→「インポート」をクリック
3. 「一般」→「既存プロジェクトをワークスペースへ」を選択し「次へ」をクリック
4. 「ルート・ディレクトリーの選択」の「参照」で「~/git/oPEN/oPEN」を開く
5. 「完了」をクリック

## プロジェクトについて

oPEN は 大阪市立大学 大学院創造都市研究科 松浦研究室のプロジェクトです。

* **主原 佑記** (*Yuki Shuhara*)
* **赤井 昭仁** (*Akihito Akai*)
* **稲葉 夏希** (*Natsuki Inaba*)
* **青谷 太志** (*Futoshi Aotani*)
* **中村 亮太** (*Ryota Nakamura*)
* **松浦 敏雄** (*Toshio Matsuura*)


[openblocks]: http://education.mit.edu/openblocks "OpenBlocks"
[eclipse]: http://eclipse.org/ "Eclipse"
[git]: http://git-scm.com/ "git"
[maven]: http://maven.apache.org/ "Apache Maven"