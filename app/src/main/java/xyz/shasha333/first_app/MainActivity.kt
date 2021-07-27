package xyz.shasha333.first_app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    /*後で変わりそうなものを定数として宣言開始*/
    val buttonnumber=5//試験管ボタンの数
    val boxnumber=4 //１試験管ごとの色付きBOXの個数
    /*後で変わりそうなものを定数として宣言終了*/
    /*今後使いそうな要素を変数として宣言開始*/
    var boxcolor = Array(buttonnumber, { Array<Int>(boxnumber, { 0 }) })//現状の色番号を保存する用(0=非表示,1～色を対応させる)
    var selectingstate=-1//現在の選択状況を示す(0=選択なし, other=選択済み移動元試験管番号)
    var errorcode=0//エラーコード()
    /*今後使いそうな要素を変数として宣言終了*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*boxcolorの初期化開始*/
        for(i in 0..buttonnumber-1){
            for(j in 0..boxnumber-1){
                boxcolor[i][j]=when(i){
                    0 -> 1
                    1 -> 2
                    2 -> 3
                    3 -> 0
                    4 -> 0
                    5 -> 0
                    6 -> 0
                    7 -> 0
                    else->0
                }
            }
        }
        /*boxcolorの初期化終了*/
        for (i in 0..buttonnumber-1) {//ボタンのリスナー作成
            val strId = resources.getIdentifier(
                "button0" + (i + 1).toString(),
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
                "button0" + (i + 1).toString(),
                "id",
                getPackageName()
            )//buttonのId取ってくる
            if(view.getId()==strId){//Idが押されたボタンと一致するボタンがあったら
                if(selectingstate==-1){//移動元が選択されていない場合
                    selectingstate=i//今回押されたものを移動元として保存
                }else if(selectingstate==i){//既に選択されているのにまた押された場合
                    //何もしない。
                }else{//移動元が決まっていた場合
                    boxmove(selectingstate, i) //移動操作
                    selectingstate=-1//未選択状態に戻す
                }
            }
        }
    }

    fun boxmove(from: Int, to: Int){//移動元と移動先のボタン番号から移動操作をする
        if(topindex(to)==0 || topindex(from)==4){//行き先が満タンな場合または、移動元が空からの場合
            //なにもしない
        }else {//移動可能であるとき
            val toindex=arrayOf<Int>(to, topindex(to) - 1)//移動先のボールの座標（0,0）
            val fromindex=arrayOf(from, topindex(from))//移動元の座標（0,0）
            boxcolor[toindex[0]][toindex[1]] = boxcolor[fromindex[0]][fromindex[1]]//数値的に移動する
            boxcolor[fromindex[0]][fromindex[1]] = 0//移動元を消す

            var fromId = resources.getIdentifier(
                "boll0" + (fromindex[0] + 1).toString()+"0"+(fromindex[1]+1).toString(),
                "id",
                getPackageName()
            )//移動元のballのId取ってくる
            findViewById<ImageView>(fromId).setVisibility(View.INVISIBLE)//移動元の画像を非表示にする
            val toId = resources.getIdentifier(
                "boll0" + (toindex[0] + 1).toString()+"0"+(toindex[1]+1).toString(),
                "id",
                getPackageName()
            )//移動先のballのId取ってくる
            val toImage= findViewById<ImageView>(toId)//移動先のViewを持ってくる
            val toDrawable = resources.getDrawable(indextocolor(boxcolor[toindex[0]][toindex[1]] ))//置き換える画像を取ってくる
            toImage.setImageDrawable(toDrawable) //移動先の画像を所定の色に変える
            toImage.setVisibility(View.VISIBLE)//移動先の画像を表示する
        }
    }

    fun colorrefresh(){//ボールを記憶通りに再表示
    }
    fun topindex(buttonindex: Int):Int{//指定された試験管内の最上の数値を返す。（0=最上段にある）
        var ans=0
        for(j in 0..boxnumber-1) {
            val strId = resources.getIdentifier(
                "boll0" + (buttonindex + 1).toString() + "0" + (j + 1).toString(),
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
            else->R.drawable.yellow
        }
        return ans
    }

    fun colortoindex(col: Int):Int{//boxのファイル名から色indexを導き出す

        return 0
    }

    fun inttostring2char(a: Int):String{//2桁以下の数字を1桁だったら0を付加する

        return ""
    }
}
