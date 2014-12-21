package com.example.highlight;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	ArrayAdapter<String> myadapter = null;
	ArrayList<String> mylist = new ArrayList<String>();
	
	//File dir = Environment.getDataDirectory();//得到/data目录，下面继续追加
	//File newFile1 = new File(dir,"/data/com.example.highlight_2/test.txt");
	//File newFile2 = new File(dir,"/data/com.example.highlight_2/nce4_words2.txt");

    LessonDBHelper database1 = new LessonDBHelper(this);
    WordDBHelper database2 = new WordDBHelper(this);
    SQLiteDatabase db1 = null;
    SQLiteDatabase db2 = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListView lv = (ListView)findViewById(R.id.lv1);
    	for(int i=0;i<48;++i){
    		int j=i+1;
    		mylist.add("Lesson "+j);
    	} 
    	myadapter = new ArrayAdapter<String>(this,R.layout.listitem,mylist);
    	lv.setAdapter(myadapter);   	
        lv.setOnItemClickListener(new OnItemClickListenerImpl()); 

        //读取newFile,写入数据库
        try{          	
        	db1 = database1.getReadableDatabase();
        	Cursor c = db1.query("lesson",null,null,null,null,null,null);
        	if(c.getCount()<48) //lesson已存在，不插入
        	{       	
	        	//FileReader fr = new FileReader(newFile1);
        		InputStreamReader ir = new InputStreamReader(getResources().openRawResource(R.raw.nce4));
	        	BufferedReader br = new BufferedReader(ir);
	        	
	          	//contents为全部内容，seg标记每段lesson
	        	String contents="";
	            String temp="";	 	
	            int lesson_num = 1;
	        	String seg = "";
		  		  int start = 0;
		  		  int end = 0;
	        	do{
	        		contents=br.readLine();
	        		temp+=contents+'\n';	
	        	}while(contents!=null);
	  
		  		  while(start>=0 && end>=0)
		  		  {
		  			
		  			  start = temp.indexOf("Lesson");
		  			  end =temp.substring(start+1).indexOf("Lesson");//end为'l'索引-1
		  			  
		  			  //读至最后一段
		  			  if(end==-1){
		  				  seg = temp;
		  			  }else{
		  			  seg=temp.substring(start,start+end+1);//从start开始，end+1前一个即end结束
		  			  temp=temp.substring(start+end+1);//从下一个lesson开始
		  			  }
		  			  
		  			  //去掉lesson前的Unit行
		  			  int rmline = 0;
		  			  if((rmline=seg.indexOf("Unit"))!=-1){
		  				  seg=seg.substring(0,rmline)+seg.substring(rmline+6);
		  			  }
	
			        	//插入lesson表
			  			ContentValues cv = new ContentValues();
			  			cv.put("id", lesson_num);
			  			cv.put("content", seg);
			  			lesson_num+=1;
			  		
			  			if(db1.insert("lesson", null, cv)==-1){
			  				System.out.println("insert failed!");}
		        	br.close();
		        	ir.close();
		  		  }
	        	
	  		  }
        	
        	db2 = database2.getReadableDatabase();
        	
        	//FileReader fr = new FileReader(newFile2);
        	InputStreamReader ir = new InputStreamReader(getResources().openRawResource(R.raw.nce4_words));
        	BufferedReader br = new BufferedReader(ir);
        	ContentValues cv = new ContentValues();
        	String contents = "";
        	String word = "";
        	int level = 0;
        	
        	if(tableIsExist("words")==false)//words已存在，不重复插入
        	{
	        	do{
	        		contents=br.readLine();
	        		//System.out.println(contents);
	        		int sep_pos = contents.indexOf(",");
	        		word=contents.substring(0,sep_pos);
	        		level = Integer.parseInt(contents.substring(sep_pos+1));
	        		
	        		cv.put("word", word);
		  			cv.put("level", level);
		  			if(db2.insert("words", null, cv)==-1){
		  				System.out.println("word insert failed!");}
	        		
	        	}while(contents!=null);
		  			  
		        	br.close();
		        	ir.close();
        	}
          
           }catch(FileNotFoundException e){
        	   e.printStackTrace();
        	   Toast.makeText(this, "文件没有找到", 5).show();
           }catch(Exception e){   
              e.printStackTrace();           
           }      	
    }
    
    //item点击监听类
	private class OnItemClickListenerImpl implements OnItemClickListener {
	
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
		long id) {
		int num = (int) MainActivity.this.myadapter.getItemId(position);//从0开始
		Intent intent = new Intent(MainActivity.this, LessonActivity.class);
		intent.putExtra("lesson_number",num);
		startActivityForResult(intent, 0);
		}
	    
	}

	//判断table是否存在
	 public boolean tableIsExist(String tableName){
         boolean result = false;
         if(tableName == null){
                 return false;
         }
         SQLiteDatabase db = null;
         Cursor cursor = null;
         db = database2.getReadableDatabase();
         String sql = "select count(*) from "+tableName;
         cursor = db.rawQuery(sql, null);
         if(cursor.moveToNext()){
                 int count = cursor.getInt(0);
                 if(count>0){
                         result = true;
                 }
         }
         return result;
	 }
   
}