package xyz.shasha333.first_app

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    /*後で変わりそうなものを定数として宣言開始*/
    val buttonnumber=5//試験管ボタンの数
    val colornumber=buttonnumber-2//色付きのボールの種類
    val boxnumber=4 //１試験管ごとの色付きBOXの個数
    /*後で変わりそうなものを定数として宣言終了*/
    /*今後使いそうな要素を変数として宣言開始*/
    var beforeaction=Array(5,{Array(4,{-1})})//戻れるように動きを記憶しておく(5回分のarrayOf{fromtube,frombox,totube,tobox}で0が古い)
    var boxcolor = Array(buttonnumber, { Array<Int>(boxnumber, { 0 }) })//現状の色番号を保存する用(0=非表示,1～色を対応させる)
    var selectingstate=-1//現在の選択状況を示す(0=選択なし, other=選択済み移動元試験管番号)
    var errorcode=0//エラーコード()
    /*今後使いそうな要素を変数として宣言終了*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firstcondition()//ボールの初期配置を決定
        refresh()//決定した初期配置通りにボールを移動
        /*boxcolorの初期化終了*/
        for (i in 0..buttonnumber-1) {//ボタンのリスナー作成
            val strId = resources.getIdentifier(
                "button" + zerohuka1to2(i + 1),
                "id",
                getPackageName()
            )//Id取ってくる
            val button = findViewById<ImageButton>(strId)//View取ってくる
            button.setOnClickListener(){//リスナーのセット
                pusshedbutton(it)
            }
        }
    }

    fun pusshedbutton(view: View){//試験管ボタンが押されたときの反応
        for (i in 0..buttonnumber-1) {
            val strId = resources.getIdentifier(
                "button" + zerohuka1to2(i + 1),
                "id",
                getPackageName()
            )//buttonのId取ってくる
            if(view.getId()==strId){//Idが押されたボタンと一致するボタンがあったら
                if(selectingstate==-1){//移動元が選択されていない場合
                    if(topindex(i)==4){//移動元が空の場合
                        toast("ここには何も入っていません")
                    }else {//何かしらが入っていれば
                        selectingstate = i//今回押されたものを移動元として保存
                        animation(i,1)//選択したため、強調する
                    }
                }else if(selectingstate==i){//既に選択されているのにまた押された場合
                    toast("同じところには移動できません")
                    selectingstate=-1//未選択状態に戻す
                    animation(i,0)//選択解除したため、強調も解除
                }else{//移動元が決まっていた場合
                    if(topindex(i)==0){//移動先が満タンの場合
                        toast("満タンで移動できません。")
                    }else{
                        if(topindex(i)>=boxnumber){//移動先が空っぽなら
                            boxmove(selectingstate, i) //移動操作
                            actionrecord(selectingstate,topindex(selectingstate)-1,i,topindex(i))//履歴として保存
                        }else if(boxcolor[selectingstate][topindex(selectingstate)]==boxcolor[i][topindex(i)]) {//移動元ボールが移動先ボールの色と一緒だったら移動する。
                            boxmove(selectingstate, i) //移動操作
                            actionrecord(selectingstate,topindex(selectingstate)-1,i,topindex(i))//履歴として保存
                        }else{
                            toast("同色でなければ移動できません")
                        }
                    }
                    selectingstate=-1//未選択状態に戻す
                    animation(i,0)//選択解除したため、強調も解除
                }
            }
        }
    }
    fun actionrecord(fromtube:Int,frombox:Int,totube:Int,tobox:Int){//1つ前の移動を記憶する。最大5つ。
        val recentaction=arrayOf(fromtube,frombox,totube,tobox)//forで入れやすくするために配列にしておく
        for(i in 0..3) {//4回分を古い方へ(0の方へ)ずらして直前アクションを入れる空白を作る。
            beforeaction[i]=beforeaction[i+1]
        }
        for(i in 0..3) {
            beforeaction[4][i]=recentaction[i]//最新版を代入する
        }
        Log.d("ログの記憶内容",beforeaction[4].contentToString())
    }

    fun boxmove(from: Int, to: Int){//移動元と移動先のボタン番号から移動操作をする
        val toindex=arrayOf<Int>(to, topindex(to) - 1)//移動先のボールの座標（0,0）
        val fromindex=arrayOf(from, topindex(from))//移動元の座標（0,0）
        boxcolor[toindex[0]][toindex[1]] = boxcolor[fromindex[0]][fromindex[1]]//数値的に移動する
        boxcolor[fromindex[0]][fromindex[1]] = 0//移動元を消す
        val fromId = resources.getIdentifier(
            "boll" + zerohuka1to2(fromindex[0] + 1)+zerohuka1to2(fromindex[1]+1),
            "id",
            getPackageName()
        )//移動元のballのId取ってくる
        findViewById<ImageView>(fromId).setVisibility(View.INVISIBLE)//移動元の画像を非表示にする
        val toId = resources.getIdentifier(
            "boll" + zerohuka1to2(toindex[0] + 1)+zerohuka1to2(toindex[1]+1),
            "id",
            getPackageName()
        )//移動先のballのId取ってくる
        val toImage= findViewById<ImageView>(toId)//移動先のViewを持ってくる
        val toDrawable = resources.getDrawable(indextocolor(boxcolor[toindex[0]][toindex[1]] ))//置き換える画像を取ってくる
        toImage.setImageDrawable(toDrawable) //移動先の画像を所定の色に変える
        toImage.setVisibility(View.VISIBLE)//移動先の画像を表示する
        if(completecheck(toindex[0])){//移動先のtubeが完成していたら
            if(allcompletecheck()){//全てが完成していたら
                toast("全てのボトルが完成！！おめでとー")
            }else{//移動先だけだったら
                toast((toindex[0]+1).toString()+"本目のボトルが完成！！！")
            }

        }
    }

    fun animation(tubenum:Int,updown:Int){//bottleの番号を受け取ってそれを選択中のアニメーションをする。（updown: 0=down, 1=up）
        var yaxis=-50F
        if(updown==0){yaxis=yaxis*-1}//downだったら-にする
        val translate= TranslateAnimation(0F,0F,0F,yaxis)
        translate.setDuration(100)//移動にかかる時間を規定
        val strId = resources.getIdentifier(
            "bottle" + zerohuka1to2(tubenum+ 1),
            "id",
            getPackageName()
        )// 移動させるbottleのIｄを獲得
        findViewById<View>(strId).startAnimation(translate)//移動開始
    }
    fun topindex(buttonindex: Int):Int{//指定された試験管内の最上の数値を返す。（0=最上段にある）
        var ans=0
        for(j in 0..boxnumber-1) {
            val strId = resources.getIdentifier(
                "boll" + zerohuka1to2(buttonindex + 1) + zerohuka1to2(j + 1),
                "id",
                getPackageName()
            )//Id取ってくる
            if(boxcolor[buttonindex][j]==0){//入っていなかったら
                ans++
            }
        }
        return ans
    }

    fun indextocolor(index: Int):Int{//色index番号をboxのファイルidに変える
        var ans=R.drawable.yellow
        ans=when(index){
            1->R.drawable.red
            2->R.drawable.blue
            3->R.drawable.yellow
            4->R.drawable.yellow
            5->R.drawable.yellow
            6->R.drawable.yellow
            else->R.drawable.yellow
        }
        return ans
    }
    fun zerohuka1to2(a: Int):String{//1文字から2文字に変えるa=ゼロを付加する数値
        var b=""
        if(a<0){//負の場合の対策
            return "00"
        }else  if(a<10){
            b="0"+a.toString()
            return b
        }else{
            return a.toString()
        }
    }//1文字から2文字に変えるa=ゼロを付加する数値
    var mToast: Toast? =null//こんなところで、一応Classとして宣言していることになるからstatic的な。
    fun toast(msg: String){
        if(mToast != null){//既に出ていた場合はキャンセル
            mToast?.cancel();
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_LONG)
        mToast?.show()
    }//引数の文字列のトーストを出す
    fun refresh(){//全てのboxの色と表示状態を更新する
        for (i in 0..buttonnumber - 1) {//tubeの数だけ回す
            for (j in 0..boxnumber - 1) {//ボールの数だけ回す
                val boxId = resources.getIdentifier(
                    "boll" + zerohuka1to2(i + 1) + zerohuka1to2(j + 1),
                    "id",
                    getPackageName()
                )//移動元のballのId取ってくる
                if(boxcolor[i][j]==0) {//非表示状態だったら
                    findViewById<ImageView>(boxId).setVisibility(View.INVISIBLE)//非表示で解決
                }else{//表示される予定だったら
                    findViewById<ImageView>(boxId).setVisibility(View.VISIBLE)//とにかく表示
                    val toDrawable = resources.getDrawable(indextocolor(boxcolor[i][j] ))//置き換える画像を取ってくる
                    findViewById<ImageView>(boxId).setImageDrawable(toDrawable) //移動先の画像を所定の色に変える
                }
            }
        }
    }
    fun firstcondition() {//boxcolor二次配列に初期位置を代入する。
        val count = Array<Int>(boxnumber, { 0 })//その色が何個出てきたか確認
        for (i in 0..colornumber - 1) {//埋めるtubeの数だけ回す
            for (j in 0..boxnumber - 1) {//ボールの数だけ回す
                while(true) {//ifの中に入るまで周り続ける
                    val rnds = (1..colornumber).random()//乱数を作成
                    if (count[rnds] < boxnumber) {//ボールのmax個数よりも少なかったら
                        boxcolor[i][j] = rnds//代入を許可する
                        count[rnds]++//入っている個数をカウント
                        break
                    }
                }
            }
        }
    }
    /*以下南専属*/
    /*
    ・この関数の内容
    上で宣言しているこれ。　boxcolor:Int = Array(buttonnumber, { Array<Int>(boxnumber, { 0 }) })//現状の色番号を保存する用(0=非表示,1～色を対応させる)
    を使って1つのtubeに入っているボールが同色かを確認する。揃っていたらtrueを返事
    ・boxcolor変数の使い方
    boxcolor[Int][Int]で数字を引き出せる(2次元配列で検索すれば出てくる)
    １つ目の[]はtubeの番号(0～)
    2つ目の[]はボールの上からの番号(0～)　ex:3つ目のtubeの上から2つ目の色番号 = boxcolor[2][1]
    ・注意事項
    もし、今後変わってきそうな数字を使う場合は一番上にある定数を使うこと。現時点での定数は以下の二つ
    val buttonnumber=5//試験管ボタンの数
    val boxnumber=4 //１試験管ごとの色付きBOXの個数
    ifを使う場合は出来るだけelseも一緒に使って、予定外のものはerrorcod:Intに値を代入
    */
    fun completecheck(tubenum:Int):Boolean{//指定のtube(0～)の色が揃ったかを確認する

        return false
    }
    fun allcompletecheck():Boolean{//全てのtubeの色が揃っていたらtrueを返事

        return false
    }
    /*以上南専属*/

}
