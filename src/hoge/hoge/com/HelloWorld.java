package hoge.hoge.com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloWorld extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
    public HelloWorld() {
        super();
    }
    // ログファイル名
    private static String LOG_FILE_NAME= "E:\\bbslog.log";
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=iso-2022-jp");
        PrintWriter out = response.getWriter();
        request.setCharacterEncoding("Shift-JIS");//SJISで受け取る

        StringBuffer sb = new StringBuffer();
        StringBuffer sb3 = new StringBuffer();

        // ログから読み出し、HTMLタグをつける
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        sb.append("<html>");
        sb.append("<meta http-equiv=\"Content-Type\" Content=\"text/html;charset=iso-2022-jp\">");
        sb.append("<head>");
        sb.append("<title>一行掲示板</title>");
        sb.append("</head>");
        sb.append("<body>");

        //フォーム欄
        sb.append("<h1>");
        sb.append("一行掲示板");
        sb.append("</h1>");

        sb.append("<form action=\"./HelloWorld\" method=\"post\">");
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<td>名前</td><td><input type=\"text\" size=\"20\" value=\"\" name=\"name\"></td></tr>");
        sb.append("<tr>");
        sb.append("<td>Mail</td><td><input type=\"text\" size=\"40\" value=\"\" name=\"mail\"></td></tr>");
        sb.append("<tr>");
        sb.append("<td>タイトル</td><td><input type=\"text\" size=\"40\" value=\"\" name=\"title\"></td></tr>");
        sb.append("<tr>");
        sb.append("<td>本文</td><td><input type=\"text\" size=\"100\" value=\"\" name=\"text\"><td></tr>");
        sb.append("<tr>");
        sb.append("<td>本文色</td>");
        sb.append("<td>");
        sb.append("<input type=\"radio\" name=\"color\" value=\"black\" checked><font color=\"black\">黒</font>");
        sb.append("<input type=\"radio\" name=\"color\" value=\"red\"><font color=\"red\">赤</font>");
        sb.append("<input type=\"radio\" name=\"color\" value=\"blue\"><font color=\"blue\">青</font>");
        sb.append("<input type=\"radio\" name=\"color\" value=\"green\"><font color=\"green\">緑</font>");
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<input type=\"submit\" name=\"button1\" value=\"送信\">");
        sb.append("</form>");
        sb.append("<hr width=\"95%\">");

        out.println(new String(sb));

        //読み出し
        try{

            String line;


            BufferedReader logres = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(LOG_FILE_NAME),"JISAutoDetect")
            );

            //ログから書き込みを１つずつ読み出していく
            while((line = logres.readLine())!=null){

                //書き込みの内容は配列で管理(1レスにつき1行)
                StringTokenizer Res = new StringTokenizer(line,"%|%");
                StringBuffer sb2 = new StringBuffer();

                int itemNum = Res.countTokens();
                String[] item2 = new String[itemNum];

                for(int i=0;i<itemNum;i++){
                    item2[i] = Res.nextToken(); //区切り文字で区切られたアイテムを１つずつロード
                }

                //タイトル
                sb2.append("<p>");
                sb2.append("<b><big>");
                sb2.append(item2[3]);    //タイトル
                sb2.append("</big></b>");

                //名前＋メールアドレス
                sb2.append("　名前：");
                if(!item2[2].equals("None")){
                    sb2.append("<a href=\"mailto:");
                    sb2.append(item2[2]);    //メール
                    sb2.append("\">");
                    sb2.append(item2[1]);    //名前
                    sb2.append("</a>");
                }
                else{
                    sb2.append(item2[1]);
                }

                //タイトルと日付
                sb2.append("　投稿日：");
                sb2.append(item2[0]);    //日付
                sb2.append("<br>");

                //本文
                sb2.append("<font color=\"");
                sb2.append(item2[5]);    //色
                sb2.append("\">　");
                sb2.append(item2[4]);    //本文
                sb2.append("</font>");
                sb2.append("</p>");

                out.println(new String(sb2));

            }

            logres.close();

            sb3.append("</body>");
            sb3.append("</html>");
            out.println(new String(sb3));

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    // doGetの送信を押すと呼び出される
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException{

        response.setContentType("text/html;charset=iso-2022-jp");
        request.setCharacterEncoding("iso-2022-jp");
        PrintWriter out = response.getWriter();

        StringBuffer NewRes = new StringBuffer();


        //書き込みデータの取得

        //日時取得の定義
        SimpleDateFormat date1 =new SimpleDateFormat("yyyy'年'MM'月'dd'日' HH:mm:ss");
        Date resDate = new Date();

        //各アイテムの取得
        String name = request.getParameter("name");
        String mail = request.getParameter("mail");
        String title = request.getParameter("title");
        String color = request.getParameter("color");
        String text = request.getParameter("text");
        String time = date1.format(resDate).toString();

        //欄が空白のときの処理
        if(name.isEmpty() == true) name = "名無しさん";
        if(mail.isEmpty() == true) mail = "None";
        if(title.isEmpty() == true) title = "(No Title)";
        if(text.isEmpty() == true) text = " ";

        //ログファイルに新しい書き込みの情報を追加
        try{
            BufferedWriter newres = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(LOG_FILE_NAME,true),"Shift-JIS")
            );

            //区切り文字は%|%
            newres.write(time+"%|%"+name+"%|%"+mail+"%|%"+title+"%|%"+text+"%|%"+color);

            newres.newLine();

            newres.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }



        //確認の表示
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        out.println("<html>");
        out.println("<meta http-equiv=\"Content-Type\" Content=\"text/html;charset=iso-2022-jp\">");
        out.println("<head>");
        out.println("<title>入力確認</title>");
        out.println("</head>");

        out.println("<body>");

        NewRes.append("<p>");
        NewRes.append("<b><big>");
        NewRes.append(title);
        NewRes.append("</big></b>");
        NewRes.append("　名前：");
        NewRes.append(name);
        NewRes.append("　投稿日：");
        NewRes.append(time);
        NewRes.append("<br>");
        NewRes.append("<font color=\"");
        NewRes.append(color);
        NewRes.append("\">");
        NewRes.append(text);
        NewRes.append("</font></p>");

        out.println(new String(NewRes));

        out.println("<p>");
        out.println("上記の結果を受け取りました");
        out.println("</p>");
        out.println("<form method=\"get\" action=\"./HelloWorld\">");
        out.println("<input type=\"submit\" value=\"掲示板に戻る\">");
        out.println("</forn>");
        out.println("</body>");
        out.println("</html>");

        out.close();


    }
}
