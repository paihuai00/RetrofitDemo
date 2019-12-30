package com.netlibrary.net_utils;

import android.support.annotation.WorkerThread;
import android.util.Log;
import com.netlibrary.impls.DownLoadImpl;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import okhttp3.ResponseBody;

/**
 * Date: 2019/12/23
 * create by cuishuxiang
 * description: Retrofit 下载Utils
 *
 * 回调{@link DownLoadImpl}
 *
 * 断点续传 Range 获得：{@link DownLoadUtils#getUrlRange(String, String, String)}
 */
public class DownLoadUtils {
    private static final String TAG = "DownLoadUtils";

    private static CompositeDisposable mCompositeDisposable;

    /**
     * 下载方法
     * @param observable  由于下载文件，文件可能过大，将保存到sd卡操作放到 子线程；所以进度回调也在子线程
     * @param fileDri    文件包
     * @param fileName  文件名
     * @param downLoad 下载回调
     * @return
     */
    public static Disposable downLoad(Observable<ResponseBody> observable, final String fileDri, final String fileName,
            final DownLoadImpl downLoad) {
        Disposable disposable =
                observable.subscribeOn(Schedulers.io()).map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        //System.out.println("aaa apply  Thread.currentThread().getName() = " + Thread.currentThread().getName());
                        return writeFile2Disk(responseBody, fileDri,fileName, downLoad);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        downLoad.onDownLoadFinish(file);
                        NetLogUtil.d(TAG + " 下载文件成功 ：file.getName() =  " + file.getName()+" file.getAbsolutePath() = "+file.getAbsolutePath());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        downLoad.onDownLoadFailed(throwable.getMessage());
                        NetLogUtil.d(TAG +"文件： "+fileName+ " 下载文件失败 ：throwable.getMessage() =  " + throwable.getMessage());
                    }
                });

        if (mCompositeDisposable==null) mCompositeDisposable = new CompositeDisposable();

        mCompositeDisposable.add(disposable);

        return disposable;
    }


    /**
     * 断点续传
     * @param observable  由于下载文件，文件可能过大，将保存到sd卡操作放到 子线程；所以进度回调也在子线程
     * @param fileDri    文件包
     * @param fileName  文件名
     * @param downLoad 下载回调
     * @return
     */
    public static Disposable downLoadWithRange(final String url,Observable<ResponseBody> observable, final String fileDri, final String fileName,
            final DownLoadImpl downLoad) {

        Disposable disposable =
                observable.subscribeOn(Schedulers.io()).map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
                        //System.out.println("aaa apply  Thread.currentThread().getName() = " + Thread.currentThread().getName());
                        return writeFile2DiskWithRange(url,responseBody, fileDri,fileName, downLoad);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        downLoad.onDownLoadFinish(file);
                        NetLogUtil.d(TAG + " 下载文件成功 ：file.getName() =  " + file.getName()+" file.getAbsolutePath() = "+file.getAbsolutePath());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        downLoad.onDownLoadFailed(throwable.getMessage());
                        NetLogUtil.d(TAG +"文件： "+fileName+ " 下载文件失败 ：throwable.getMessage() =  " + throwable.getMessage());
                    }
                });

        if (mCompositeDisposable==null) mCompositeDisposable = new CompositeDisposable();

        mCompositeDisposable.add(disposable);

        return disposable;
    }

    public static void clearDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }

    /**
     * 保存file到sd卡
     */
    @WorkerThread
    private static File writeFile2Disk(ResponseBody responseBody,String fileDir, String fileName,
            DownLoadImpl downloadListener) {
        //先创建，文件夹
        File dirFile = new File(fileDir);
        if (!dirFile.exists())dirFile.mkdirs();
        //创建 File
        File file = new File(dirFile, fileName);
        if (file.exists()){
            file.delete();// 如果文件存在，直接删除
        }

        long currentLength = 0;
        OutputStream os = null;

        if (responseBody == null) {
            NetLogUtil.d(TAG + " 文件下载： writeFile2Disk # responseBody == null，资源错误 ");
            downloadListener.onDownLoadFailed("资源错误！");
            return file;
        }
        InputStream is = responseBody.byteStream();
        long totalLength = responseBody.contentLength();

        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buff = new byte[2 * 1024];
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
                currentLength += len;
                //NetLogUtil.d(TAG + "当前进度: " + currentLength);
                int tempProgress = (int) (100 * currentLength / totalLength);
                //System.out.println(" bbbbb: " + tempProgress);
                downloadListener.onProgressCallBack(tempProgress);
                if (tempProgress == 100) {
                    //在外面回调回去
                    //downloadListener.onDownLoadFinish(file);
                }
            }
        } catch (FileNotFoundException e) {
            downloadListener.onDownLoadFailed("未找到文件！");
            e.printStackTrace();
        } catch (IOException e) {
            downloadListener.onDownLoadFailed("IO错误！");
            NetLogUtil.d(TAG + "IO错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return file;
        }
    }


    /**
     * 保存file到sd卡
     */
    @WorkerThread
    private static File writeFile2DiskWithRange(String url,ResponseBody responseBody,String fileDir, String fileName,
            DownLoadImpl downloadListener) {

        //从sp中读取到已下载的长度
        long range = 0;
        range = SPUtils.get(url, 0l);

        System.out.println("range hahah = " + range);

        //先创建，文件夹
        File dirFile = new File(fileDir);
        if (!dirFile.exists())dirFile.mkdirs();
        //创建 File
        File file = new File(dirFile, fileName);
        if (file.exists()&&range==file.length()){
            file.delete();// 如果文件存在，直接删除
        }
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;
        long total = range;
        long responseLength = 0;
        try {
            byte[] buf = new byte[2 * 1024];
            int len = 0;
            responseLength = responseBody.contentLength();
            inputStream = responseBody.byteStream();

            randomAccessFile = new RandomAccessFile(file, "rwd");
            if (range == 0) {
                randomAccessFile.setLength(responseLength);
            }
            randomAccessFile.seek(range);

            int progress = 0;
            int lastProgress = 0;

            while ((len = inputStream.read(buf)) != -1) {
                randomAccessFile.write(buf, 0, len);
                total += len;
                //在sp中保存已经下载的
                SPUtils.put(url,total);
                lastProgress = progress;
                progress = (int) (total * 100 / randomAccessFile.length());
                if (progress > 0 && progress != lastProgress) {
                    downloadListener.onProgressCallBack(progress);
                }
            }
            //downloadListener.onDownLoadFinish(file);
        } catch (Exception e) {
            NetLogUtil.d(TAG + " writeFile2DiskWithRange  错误: " + e.getMessage());
            downloadListener.onDownLoadFailed(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }

                if (inputStream != null) {
                    inputStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return file;
        }
    }

    /**
     * 获得 已下载的range
     */
    public static String getUrlRange(String url, String fileDir,String fileName) {
        try {
            //断点续传时请求的总长度
            File file = new File(fileDir, fileName);
            String totalLength = "-";
            if (file.exists()) {
                totalLength += file.length();
            }
            //从sp中读取到已下载的长度
            long range = 0;
            range = SPUtils.get(url, 0l);

            //如果file存在，并且range跟文件长度一致，即为下载完成，删除重新下载
            if (file.exists() & range == file.length()) {
                SPUtils.put(url, 0l);
                file.delete();
                return "bytes=" + "0-";
            }
            return "bytes=" + Long.toString(range) + totalLength;
        } catch (Exception e) {
            return "bytes=" + "0-";
        }
    }
}
