# 深圳乐信(Joyhonest)为SyMa开发

1 使用

compile 'com.Joyhonest:JH_Camera:1.0.1'

对于SyMa Fly
import com.joyhonest.jh_fly.Fly_PlayActivity;

import com.joyhonest.jh_ui.JH_App;

JH_App.init(getApplicationContext(),null,null,null,null);

JH_App.checkDeviceHasNavigationBar(this);

Intent mainIntent = new Intent(startActivity.this, Fly_PlayActivity.class);

startActivity(mainIntent);

对于SyMa Go

Intent mainIntent = new Intent(startActivity.this, PlayActivity.class);
startActivity(mainIntent);

JH_App.init，参数：

JH_App.init(Context contextA, String LocalPhoto, String LocalVideo, String SDPhoto, String SDVideo);

contextA = getApplicationContext();

LocalPhoto：录像到手机的的相片目录

LocalVideo：录像到手机的的视频目录

SDPhoto： 从模块SD卡上下载到手机的相片目录（如果模块不支持SD卡，可以填入null）

SDVideo： 从模块SD卡上下载到手机的视频目录（如果模块不支持SD卡，可以填入null）

如果都填入null，就是使用系统的默认值，“SYMA_Photo_JH” “SYMA_Video_JH” “SYMA_SDPhoto_JH” “SYMA_SDVideo_JH”
