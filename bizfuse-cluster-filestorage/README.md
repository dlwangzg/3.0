# 环境配置

## 安装音视频处理程序 ffmpeg

### Windows安装  
下载windows版ffmpeg，解压缩。下载地址 (http://172.17.20.95:8080/software/ffmpeg-20170605-4705edb-win64-static.zip)

### Ubuntu系统安装  

- 依赖包下载
yasm-1.3.0  下载地址 (http://172.17.20.95:8080/software/yasm-1.3.0.tar.gz)  
ffmpeg-2.8.6  下载地址 (http://172.17.20.95:8080/software/ffmpeg-2.8.6.tar.bz2)  


- 安装安装yasm  

```
	cd yasm-1.3.0/
	./configure
	make
	make install
```

- 安装lame

```
	sudo apt-get install libmp3lame-dev
```

- 安装ffmpeg

```
	cd ffmpeg-2.8.3/
	./configure --enable-libmp3lame
	make
	make install
```

## 安装图片处理程序ImageMagick

### Windows安装  
下载windows版ImageMagick   (下载地址 http://172.17.20.95:8080/software/ImageMagick-6.3.9-0-Q8-windows-static.exe)
双击.exe文件进行安装
安装完成后将安装目录添加到环境变量(默认自动添加)
### Ubuntu系统安装  
```
	sudo apt-get install imagemagick
	
```

### Ubuntu系统 安装并创建mongodb user

1.下载mongodb及配置文件，并解压
http://172.17.20.95:8080/software/mongo-config.tar
http://172.17.20.95:8080/software/mongodb-linux-x86_64-ubuntu1404-3.2.7.tgz
2.执行配置文件中的启动脚本
```
	进入/mongodb/bin$ 
	输入： 1)./mongo
	      2)use name(想要创建的mongodb的名字)
	      3)db.createUser({user:"leadingMongodb",pwd:"leadingMongodb@2017",roles:[{role:"readWrite",db:"name（上面的名字）"}]})
	
```

### Ubuntu系统安装openoffice  
```
	下载 linux服务器版本的openoffice 下载地址（http://172.17.20.95:8080/software/Apache_OpenOffice_4.1.3_Linux_x86-64_install-deb_zh-CN%20%281%29.tar.gz）
	
	安装如此链接 http://blog.csdn.net/tomcat_2014/article/details/47951377
	
```