package com.joyhonest.jh_fpv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.joyhonest.jh_ui.JH_App;
import com.joyhonest.jh_ui.R;
import com.joyhonest.jh_ui.Storage;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class grid_fpv extends AppCompatActivity implements View.OnClickListener {

     private ImageView  imageType;
     private Button btn_select;
    private Button btn_delete;
    private Button btn_exitgrid;
    private GridView gridView;
    private ViewPager photo_vp;
    private  int   index;

    private VideoView   Brow_videoView;
    MediaController mc;

    private RelativeLayout  DeleteAert;
    private Button  alart_cancel;
    private Button  alart_ok;

    private RelativeLayout Brow_videoView_Layout;
    private Button btn_ExitPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_fpv);
        JH_App.checkDeviceHasNavigationBar(this);

        Brow_videoView_Layout = (RelativeLayout)findViewById(R.id.Brow_videoView_Layout);

        Brow_videoView_Layout.setVisibility(View.GONE);

        DeleteAert = (RelativeLayout)findViewById(R.id.DeleteAert);
        alart_cancel = (Button)findViewById(R.id.alart_cancel);
        alart_ok = (Button)findViewById(R.id.alart_ok);
        DeleteAert.setVisibility(View.INVISIBLE);

        btn_ExitPlay = (Button)findViewById(R.id.btn_ExitPlay);


        local_PhotolistFiles = new ArrayList<String>();
        local_VideolistFiles = new ArrayList<String>();

        gridView = (GridView)findViewById(R.id.grid_view);

        photo_vp = (ViewPager)findViewById(R.id.photo_vp);
        photo_vp.setVisibility(View.GONE);



        imageType = (ImageView)findViewById(R.id.imageType);
        btn_select=(Button)findViewById(R.id.btn_select);
        btn_delete=(Button)findViewById(R.id.btn_delete);
        btn_exitgrid = (Button)findViewById(R.id.btn_exitgrid);

        btn_delete.setBackgroundResource(R.mipmap.delete_btn_jh_fpv_dis);
        btn_delete.setClickable(false);


        btn_select.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_exitgrid.setOnClickListener(this);
        photo_vp.setOnClickListener(this);
        photo_vp.setClickable(true);
        alart_cancel.setOnClickListener(this);
        alart_ok.setOnClickListener(this);
        btn_ExitPlay.setOnClickListener(this);



        Brow_videoView = (VideoView)findViewById(R.id.Brow_videoView);
        Brow_videoView.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams layoutParams=
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        Brow_videoView.setLayoutParams(layoutParams);

        mc = new MediaController(this);
        Brow_videoView.setMediaController(mc);




        WindowManager manger = getWindowManager();
        Display display = manger.getDefaultDisplay();
        //屏幕宽度
        Point point = new Point();
        display.getSize(point);
        int screenWidth = point.x; //display.getWidth();
        gridView.setNumColumns((int) (screenWidth / Storage.dip2px(this, 100)) - 1);

/*
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //return false;
                return true;
            }
        });
        */

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyNode node = nodes.get(position);
                boolean  bSelect=isSelectmode();
                if(bSelect)
                {

                    if(node.nSelectType==1)
                    {
                        node.nSelectType = 2;
                    }
                    else
                    {
                        node.nSelectType = 1;
                    }
                    DispDeleteIcon(CountSelected()>0);
                    myAdapter.notifyDataSetChanged();
                }
                else
                {
                    //if (MyApp.nBrowType == MyApp.Brow_Photo)
                    if(JH_App.bBrowPhoto)
                    {
                        //TitleText_View.setVisibility(View.INVISIBLE);
                        handler.removeCallbacksAndMessages(null);
                        Brow_videoView_Layout.setVisibility(View.INVISIBLE);
                        gridView.setVisibility(View.INVISIBLE);
                        final PagerAdapter adapter = new PagerAdapter() {
                            @Override
                            public int getCount() {
                                return nodes.size();
                            }

                            @Override
                            public boolean isViewFromObject(View arg0, Object arg1) {
                                return arg0 == arg1;
                            }

                            /**
                             * 销毁当前page的相隔2个及2个以上的item时调用
                             */
                            @Override
                            public void destroyItem(ViewGroup container, int position, @NonNull Object o) {
                                //container.removeViewAt(position);
                                container.removeView((View) o);
                            }

                            //设置ViewPager指定位置要显示的view
                            @Override
                            public Object instantiateItem(ViewGroup container, int position) {
                                final MyNode node = nodes.get(position);
                                ImageView im = new ImageView(grid_fpv.this);
                                Bitmap bmpa = LoadBitmap(node.sPath);
                                im.setBackground(new BitmapDrawable(getResources(), bmpa));
                                im.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        gridView.setVisibility(View.VISIBLE);
                                        photo_vp.setVisibility(View.GONE);
                                    }
                                });
                                container.addView(im);
                                return im;
                            }
                        };
                        photo_vp.setAdapter(adapter);
                        photo_vp.setCurrentItem(position);
                        photo_vp.setVisibility(View.VISIBLE);


                    } else {

                        Brow_videoView_Layout.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.INVISIBLE);
                        Brow_videoView.setVideoPath(node.sPath);
                        Brow_videoView.start();
                        Brow_videoView.requestFocus();
                        mc.hide();
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(runnable,100);

                    }
                }
            }
        });

        bExit = false;
        _Init_Theard init = new _Init_Theard();
        init.start();
        EventBus.getDefault().register(this);

    }


    private  void DispDeleteIcon(boolean b)
    {
        if(!b)
        {
            btn_delete.setBackgroundResource(R.mipmap.delete_btn_jh_fpv_dis);
            btn_delete.setClickable(false);
        }
        else
        {
            btn_delete.setBackgroundResource(R.mipmap.delete_btn_jh_fpv);
            btn_delete.setClickable(true);
        }
    }

    private int CountSelected()
    {
        if(nodes==null)
            return 0;
        int nCount = 0;
        for(MyNode node:nodes)
        {
            if(node.nSelectType == 2)
            {
                nCount++;
            }
        }
        return nCount;
    }

    private Bitmap LoadBitmap(String sPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(sPath, options); //此时返回bm为空
        options.inJustDecodeBounds = false;
        //计算缩放比
        int be = (int) (options.outHeight / (float) 1280);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be;
        //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
        bitmap = BitmapFactory.decodeFile(sPath, options);
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        if(v==btn_exitgrid)
        {
            bExit = true;
            finish();
        }
        if(v==btn_select)
        {
            if(!isSelectmode())
            {
                setSelectMode(true);
            }
            else
            {
                setSelectMode(false);
            }
            DispDeleteIcon(CountSelected()>0);
        }
        if(v==btn_delete)
        {
            DeleteAert.setVisibility(View.VISIBLE);
        }
        if(photo_vp == v)
        {
            photo_vp.setVisibility(View.GONE);
        }
        if(alart_cancel==v)
        {
            DeleteAert.setVisibility(View.INVISIBLE);
        }
        if(alart_ok==v)
        {
            deleteSelectFile();
            DeleteAert.setVisibility(View.INVISIBLE);
            resetdeleteSelectFile();
            DispDeleteIcon(CountSelected()>0);
        }
        if(btn_ExitPlay==v)
        {
            F_ExitPlayVideo();
        }

    }


    private void deleteSelectFile()
    {

        if(nodes.size()==0)
            return;
        int size = nodes.size()-1;
        for(int ix=size;ix>=0;ix--)
        {
            MyNode node = nodes.get(ix);
            String sPath = node.sPath;
            if(node.nSelectType==2)
            {
                nodes.remove(ix);
                if (ix < mList.size())
                    mList.remove(ix);

                JH_App.DeleteImage(sPath);
                String strA = getFileName(sPath);
                String strs = getCacheDir() + strA + ".thb.png";
                if(!JH_App.bBrowPhoto)
                {
                    strs = getCacheDir() + "/" + strA + ".v_thb.png";
                }
                File ff = new File(strs);
                if (ff.exists()) {
                    ff.delete();
                }
                /*
                if(nodes.size() == 0) {
                    //TitleText_View.setText(R.string.NoFile);
                }
                else
                {
                    String sFormat=Grid_View.this.getString(R.string.Total_files);
                    String s  = String.format(sFormat,nodes.size());
                    TitleText_View.setText(s);
                }
                */
                myAdapter.notifyDataSetChanged();
            }


        }

        resetdeleteSelectFile();
    }

    private void resetdeleteSelectFile()
    {

        for(MyNode node :nodes)
        {
            node.nSelectType = 0;
        }
        myAdapter.notifyDataSetChanged();

    }

    private void setSelectMode(boolean b)
    {
        if(nodes!=null)
        {
            for(MyNode node :nodes)
            {
                if(b) {
                    node.nSelectType = 1;
                }
                else
                {
                    node.nSelectType = 0;
                }
            }
        }
        myAdapter.notifyDataSetChanged();

    }

    private boolean isSelectmode()
    {
        boolean bisSelectmode=false;
        if(nodes!=null)
        {
            for(MyNode node :nodes)
            {
                if(node.nSelectType!=0)
                {
                    bisSelectmode = true;
                    break;
                }
            }

        }
        return bisSelectmode;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JH_App.checkDeviceHasNavigationBar(this);
    }

    //自定义适配器
    class MyAdapter extends BaseAdapter {
        //上下文对象
        private Context context;
        private int viewResourceId;
        private List<MyNode> mfilelist;
        private LayoutInflater mInflater;


        MyAdapter(Context context, int resourceid, List<MyNode> objects) {
            this.context = context;
            this.viewResourceId = resourceid;
            this.mfilelist = objects;
            this.mInflater = LayoutInflater.from(context);
            /*
            if(MyApp.bScreenRota)
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            else
            {


                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            */
        }

        public int getCount() {
            return mfilelist.size();
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        //创建View方法
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder holder = null;
            MyNode node = mfilelist.get(position);
            if (convertView == null) {
                convertView = mInflater.inflate(viewResourceId, null);
                holder = new MyViewHolder();
                holder.progressBar = (ProgressBar) convertView.findViewById(R.id.Grid_progressBar1);
                holder.progressBar.setProgress(0);
                holder.progressBar.setMax(1000);
                holder.icon = (ImageView) convertView.findViewById(R.id.Grid_imageView1);
                holder.video_bg = (ImageView) convertView.findViewById(R.id.video_bg);
                holder.caption = (TextView) convertView.findViewById(R.id.Grid_textView1);
                holder.SelectIcon = (ImageView)convertView.findViewById(R.id.image_Select);
                holder.sUrl = "";
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder) convertView.getTag();
            }
            //if(holder.sUrl!="")
            if (node != null) {
                {
                    if(node.nSelectType==1)
                    {
                        holder.SelectIcon.setVisibility(View.VISIBLE);
                        holder.SelectIcon.setBackgroundResource(R.mipmap.selectedflag_fly_0_jh);
                    }else if(node.nSelectType==2)
                    {
                        holder.SelectIcon.setVisibility(View.VISIBLE);
                        holder.SelectIcon.setBackgroundResource(R.mipmap.selectedflag_fly_jh);
                    }
                    else
                    {
                        holder.SelectIcon.setVisibility(View.INVISIBLE);
                    }
                    if (node.bitmap != null) {
                        holder.icon.setImageBitmap(node.bitmap);
                    }
                    else {
                      //  Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.background);
                        //holder.icon.setImageBitmap(bmp);
                    }
                }
                if (node.nType == MyNode.TYPE_Remote) {
                    holder.progressBar.setVisibility(View.VISIBLE);
                    if (node.nStatus == MyNode.Status_downloaded) {
                        holder.progressBar.setProgress(1000);
                        holder.caption.setVisibility(View.INVISIBLE);
                    } else if (node.nStatus == MyNode.Status_downloading) {
                        holder.progressBar.setProgress((int) node.nPre);
                        holder.caption.setTextColor(0xFF0000FF);
                        holder.caption.setVisibility(View.VISIBLE);
                        if (node.nPre < 10)
                            node.nPre = 10;
                       // String s1 = getString(R.string.downloading)+" "+node.nPre/10+"%";
                        String s1 = "Loadding"+" "+node.nPre/10+"%";
                        holder.caption.setText(s1);
                    } else {
                        holder.progressBar.setProgress(0);
                        holder.caption.setVisibility(View.INVISIBLE);
                    }
                } else {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    holder.caption.setVisibility(View.INVISIBLE);
                }
            }
            /*
            if (JH_App.nBrowType == MyApp.Brow_Video){
                holder.video_bg.setVisibility(View.VISIBLE);
            }
            */
            return convertView;
        }

        class MyViewHolder {
            ProgressBar progressBar;
            TextView caption;
            ImageView icon;
            ImageView video_bg;
            String sUrl;
            ImageView    SelectIcon;
        }
    }





    private boolean bExit = false;

    private MyAdapter myAdapter;
    private List<String> mList;
    private List<MyNode> nodes;
    private List<String> local_PhotolistFiles; // = MyApp.getInstance().local_PhotoList;
    private List<String> local_VideolistFiles; // = MyApp.getInstance().local_VideoList;


    @Subscriber(tag = "OP_Hide_Info_Dialog")
    private void OP_Hide_Info_Dialog(String str)
    {
        myAdapter = new MyAdapter(grid_fpv.this, R.layout.my_grid_node, nodes);
        gridView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        //WaitView.setVisibility(View.INVISIBLE);

    }

    private class _Init_Theard extends Thread {
        @Override
        public void run() {
            F_Init();
        }
    }

    class MapComparator implements Comparator<String> {
        public int compare(String lhs, String rhs) {
            return lhs.compareTo(rhs);
        }

    }


    private String getFileType(String filename){
        if(filename==null)
            return null;
        filename.toLowerCase();
        int pos = filename.lastIndexOf(".");
        if(pos == -1){
            return null;
        }
        return filename.substring(pos+1);
    }

    private void F_GetAllPhotoLocal() {
        String sPath = JH_App.sLocalPhoto;
        local_PhotolistFiles.clear();
        if (sPath != null) {
            File file = new File(sPath);
            File files[] = file.listFiles();
            String sExt;
            for (int i = 0; i < files.length; i++) {
                File f = files[i];

                sExt = getFileType(f.getPath());
                if(sExt!=null) {
                    if (sExt.equalsIgnoreCase("jpg") ||
                            sExt.equalsIgnoreCase("png") ||
                            sExt.equalsIgnoreCase("bmp")) {
                        local_PhotolistFiles.add(f.getPath());
                    }
                }
            }
        }
        mList = local_PhotolistFiles;
        Collections.sort(mList, new MapComparator());

    }
    private void F_GetAllVideoLocal() {
        String sPath = JH_App.sLocalVideo;
        local_VideolistFiles.clear();
        if (sPath != null) {
            File file = new File(sPath);
            File files[] = file.listFiles();

            String sExt;
            for (File f : files) {
                sExt = getFileType(f.getPath());
                if(sExt!=null) {
                    if (sExt.equalsIgnoreCase("mp4"))
                    {
                        local_VideolistFiles.add(f.getPath());
                    }
                }
            }
        }
        mList = local_VideolistFiles;
        Collections.sort(mList, new MapComparator());
    }

    private String getFileName(String pathandname) {

        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1);
        } else {
            return null;
        }

    }

    private void F_ExitPlayVideo()
    {
        Brow_videoView.stopPlayback();
        Brow_videoView_Layout.setVisibility(View.GONE);
        photo_vp.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.VISIBLE);
        JH_App.checkDeviceHasNavigationBar(this);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        if(Brow_videoView_Layout.getVisibility()==View.VISIBLE)
        {
            F_ExitPlayVideo();

        }
        else {
            super.onBackPressed();
        }

    }

    private void F_SaveBitmap(Bitmap bm, String sPath) {
        Log.e("WIFI", "保存图片");
        if (bm == null)
            return;
        File f = new File(sPath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i("WIFI", "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Bitmap GetSuonuitu(String sPath) {

        Bitmap bitmap = null;
        String strs = "";

        String sf = getFileName(sPath);
        strs = this.getCacheDir() + "/" + sf + ".thb.png";

        bitmap = BitmapFactory.decodeFile(strs);
        if (bitmap != null)
            return bitmap;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        bitmap = BitmapFactory.decodeFile(sPath, options); //此时返回bm为空
        options.inJustDecodeBounds = false;
        //计算缩放比
        int be = (int) (options.outHeight / (float) 100);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be;
        //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
        bitmap = BitmapFactory.decodeFile(sPath, options);
        F_SaveBitmap(bitmap, strs);
        return bitmap;
    }
    private Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        String strs = "";
        String str = getFileName(filePath);
        strs = this.getCacheDir() + "/" + str + ".v_thb.png";
        bitmap = BitmapFactory.decodeFile(strs);
        if (bitmap != null)
            return bitmap;


        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(1);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        F_SaveBitmap(bitmap, strs);
        return bitmap;
    }

    private  void F_Init()
    {

        //WaitView.setVisibility(View.VISIBLE);

        int nBrow = 0;
        if(!JH_App.bBrowPhoto)
            nBrow = 1;

        if (JH_App.bBrowPhoto)
        {

            imageType.setImageResource(R.mipmap.photofolder_nor_jh);
            F_GetAllPhotoLocal();
            if(nodes!=null)
            {
                nodes.clear();
            }
            nodes = new ArrayList<MyNode>();
            if ( mList!= null) {
                for (String sPath : mList) {
                    if(bExit)
                        break;;
                    Bitmap bitmap = GetSuonuitu(sPath);
                    MyNode node = new MyNode(nBrow);
                    node.bitmap = bitmap;
                    node.sPath = sPath;
                    nodes.add(node);
                }
            }
        }
        else
        {
            imageType.setImageResource(R.mipmap.videofolder_nor_jh);
            F_GetAllVideoLocal();
            if(nodes!=null)
            {
                nodes.clear();
            }
            nodes = new ArrayList<MyNode>();
            if ( mList!= null) {
                for (String sPath : mList) {
                    if(bExit)
                        break;;
                    Bitmap bitmap = getVideoThumbnail(sPath);
                    MyNode node = new MyNode(0);
                    node.bitmap = bitmap;
                    node.sPath = sPath;
                    nodes.add(node);
                }
            }
        }

        /*
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(nodes.size() == 0) {
                        TitleText_View.setText(R.string.NoFile);
                    }
                    else
                    {
                        String sFormat=Grid_View.this.getString(R.string.Total_files);
                        String s  = String.format(sFormat,nodes.size());
                        TitleText_View.setText(s);
                    }
                }
            });
        }
        */
        if(bExit) {
            if(nodes!=null)
            {
                nodes.clear();
            }
            return;
        }

        EventBus.getDefault().post("abd","OP_Hide_Info_Dialog");

    }

    private  void HideBar()
    {
        JH_App.checkDeviceHasNavigationBar(this);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!mc.isShowing())
                HideBar();
            handler.postDelayed(this, 800);
        }
    };

}
