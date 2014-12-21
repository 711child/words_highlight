package com.example.highlight;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


public class LessonActivity extends Activity {
	TextView tv;
	
	String lesson_content="";
	int num = 0;
	WordDBHelper database1 = new WordDBHelper(this);
	SQLiteDatabase db1 = null;
	LessonDBHelper database2 = new LessonDBHelper(this);
	SQLiteDatabase db2 = null;
	Cursor cursor = null;
	
	HashSet<String>highlightSet=new HashSet<String>();//动态highlight集合
	ArrayList<String>words=new ArrayList<String>();//lesson所有单词
	boolean highlight = false; //高亮默认关闭
	int bar_level=0; //seekbar手动滑动的级别
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        db1 = database1.getReadableDatabase();      
    	db2 = database2.getReadableDatabase();
    	
    	tv =(TextView)findViewById(R.id.tv1);
    	Intent intent = getIntent();
    	num = intent.getIntExtra("lesson_number", 0); 	
    	//取得lesson文本
    	Cursor c = db2.query("lesson",null,null,null,null,null,null);
    	if(c.moveToFirst()){
    		c.move(num);
    		int index = c.getColumnIndex("content");
        	lesson_content = c.getString(index);
    	}	
    	c.close();
    	db2.close();
    	
    	tv.setText(lesson_content);
    	
    	String[] words_copy =lesson_content.split("\\s+");
    	//对于每个词，在word.db中查找，若找到且level<=seekbar响应事件的level，高亮
    	for(String word:words_copy)
    	{
    		words.add(word);
    		cursor = db1.rawQuery("select * from words where word=? COLLATE NOCASE",  new String[]{word});
    
    		if (cursor!=null && cursor.getCount()>0) { 
    			cursor.moveToFirst();
    			int level = cursor.getInt(1); //获取第2列level值
    		    if(level<=bar_level && highlight){
    		    	highlightSet.add(word);
    		        } 
    		}
    	}

    	
		//Switch,高亮开关
    	 Switch open = (Switch)findViewById(R.id.switch1);
     	
	     open.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				
				if (isChecked) {
					if(bar_level==0)
					{
						//初始为0时没有触发滑动，也点亮
						for(String word:words)
				    	{
				    		cursor = db1.rawQuery("select * from words " +
				    				"where word=? COLLATE NOCASE", 
				    				new String[]{word});
				    		if (cursor!=null && cursor.getCount()>0) 
				    		{ 
				    			cursor.moveToFirst();
				    			int level = cursor.getInt(1); //获取第2列的值
				    		    if(level<=bar_level)
				    		    { //注意，即使highlight=false也添加
				    		    	highlightSet.add(word);	
				    		    } 
				    		    else 
				    		    {
				    		    	highlightSet.remove(word);
				    		    }
				    		}
				    	}
					}
					HighLight(lesson_content,highlightSet);
					highlight=true;
				} else {
					highlight=false;
					tv.setText(lesson_content);
				}
			}

		});

	     
	     
	     
	     //SeekBar,level进度条
	     SeekBar seekbar = (SeekBar)findViewById(R.id.seekbar);
	     seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {	
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
				//滑动停止时，点亮
				bar_level=seekBar.getProgress();
				for(String word:words)
		    	{
		    		cursor = db1.rawQuery("select * from words " +
		    				"where word=? COLLATE NOCASE", 
		    				new String[]{word});
		    		if (cursor!=null && cursor.getCount()>0) 
		    		{ 
		    			cursor.moveToFirst();
		    			int level = cursor.getInt(1); //获取第2列的值
		    		    if(level<=bar_level )
		    		    { //注意，即使highlight=false也添加
		    		    	highlightSet.add(word);	
		    		    } 
		    		    else 
		    		    {
		    		    	highlightSet.remove(word);
		    		    }
		    		}
		    	}
				HighLight(lesson_content,highlightSet);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
			}
		});   	
    }
   
    public void HighLight(String content,HashSet<String> set)
    {
    	SpannableString sp=new SpannableString(content);
    	for(String word:set)
    	{
    		String temp = content;
    		
    		int start=0;
        	int end=0;
        	int offset=0;//单词重复出现时距离第一次的位移
    		while(start>=0 && end>=0)
    		{
		    	start = temp.indexOf(word);
		    	end = start + word.length();
		    	if(start>=0 && end>=0)
		    	{
		    		sp.setSpan(new BackgroundColorSpan(Color.YELLOW), start+offset, end+offset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    		temp=temp.substring(end); //若单词出现不止一次，从剩下字符串中继续高亮
		    	}
		    	offset+=end;
    		}
    		
    	}
    	tv.setText(sp);
   	 
    }
    
    protected void onDestroy() {
    	  cursor.close(); 
    		db1.close(); 
    		super.onDestroy();
		
	} 
}
