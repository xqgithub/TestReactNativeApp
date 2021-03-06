package com.first.testreactnativeapp.hotupdate;

import android.content.Context;

import com.first.testreactnativeapp.constants.AppConstant;
import com.first.testreactnativeapp.constants.FileConstant;
import com.first.testreactnativeapp.utils.LogUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

/**
 * 热修复
 * Created by admin on 2018/8/15.
 */

public class HotUpdate {
    public static void checkPackage(Context context, String filePath) {
        // 1.下载前检查SD卡是否存在更新包文件夹,FIRST_UPDATE来标识是否为第一次下发更新包
        File bundleFile = new File(filePath);
        if (bundleFile != null && bundleFile.exists()) {
            ACache.get(context).put(AppConstant.FIRST_UPDATE, false);
        } else {
            ACache.get(context).put(AppConstant.FIRST_UPDATE, true);
        }
    }


    public static void handleZIP(final Context context) {

        // 开启单独线程，解压，合并。
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = (Boolean) ACache.get(context).getAsObject(AppConstant.FIRST_UPDATE);
                if (result) {
                    // 解压到根目录
                    FileUtils.decompression(FileConstant.JS_PATCH_LOCAL_FOLDER);
                    // 合并
                    mergePatAndAsset(context);
                } else {
                    // 解压到future目录
                    FileUtils.decompression(FileConstant.FUTURE_JS_PATCH_LOCAL_FOLDER);
                    // 合并
//                    mergePatAndBundle();
                    mergePatAndBundle1();
                }
                // 删除ZIP压缩包
                FileUtils.deleteFile(FileConstant.JS_PATCH_LOCAL_PATH);
            }
        }).start();
    }

    /**
     * 与Asset资源目录下的bundle进行合并
     */
    private static void mergePatAndAsset(Context context) {

        // 1.解析Asset目录下的bundle文件
        String assetsBundle = FileUtils.getJsBundleFromAssets(context);
        String aaaaa = FileUtils.getStringFromPat(FileConstant.LOCAL_FOLDER + "/index.android.bundle");

        // 对比
        diff_match_patch dmp = new diff_match_patch();
        LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(assetsBundle, aaaaa);
        // 生成差异补丁包
        LinkedList<diff_match_patch.Patch> patches = dmp.patch_make(diffs);
        // 解析补丁包
        String patchesStr = dmp.patch_toText(patches);
        try {
            // 将补丁文件写入到某个位置
            com.first.testreactnativeapp.utils.FileUtils.writeFileFromString(FileConstant.JS_PATCH_LOCAL_FILE, patchesStr, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 2.解析bundle当前目录下.pat文件字符串
        String patcheStr = FileUtils.getStringFromPat(FileConstant.JS_PATCH_LOCAL_FILE);
        // 3.合并
        merge(patcheStr, assetsBundle);
        // 4.删除pat
        FileUtils.deleteFile(FileConstant.JS_PATCH_LOCAL_FILE);
    }

    /**
     * 与SD卡下的bundle进行合并
     */
    private static void mergePatAndBundle() {

        // 1.解析sd卡目录下的bunlde
        String assetsBundle = FileUtils.getJsBundleFromSDCard(FileConstant.LOCAL_FOLDER + "/index.android.bundle");
        // 2.解析最新下发的.pat文件字符串
        String patcheStr = FileUtils.getStringFromPat(FileConstant.FUTURE_PAT_PATH);
        // 3.合并
        merge(patcheStr, assetsBundle);
        // 4.添加图片
        FileUtils.copyPatchImgs(FileConstant.FUTURE_DRAWABLE_PATH, FileConstant.DRAWABLE_PATH);
        // 5.删除本次下发的更新文件
        FileUtils.traversalFile(FileConstant.FUTURE_JS_PATCH_LOCAL_FOLDER);
    }

    private static void mergePatAndBundle1() {
        // 1.解析sd卡目录下的bunlde
        String localbundle = FileUtils.getJsBundleFromSDCard(FileConstant.LOCAL_FOLDER + "/index.android.bundle");
        String futurebundle = FileUtils.getStringFromPat(FileConstant.FUTURE_BUNDLE_PATH);
        // 对比
        diff_match_patch dmp = new diff_match_patch();
        LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(localbundle, futurebundle);
        // 生成差异补丁包
        LinkedList<diff_match_patch.Patch> patches = dmp.patch_make(diffs);
        // 解析补丁包
        String patchesStr = dmp.patch_toText(patches);
        try {
            // 将补丁文件写入到某个位置
            com.first.testreactnativeapp.utils.FileUtils.writeFileFromString(FileConstant.FUTURE_PAT_PATH, patchesStr, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 2.解析bundle当前目录下.pat文件字符串
        String patcheStr = FileUtils.getStringFromPat(FileConstant.FUTURE_PAT_PATH);
        // 3.合并
        merge(patcheStr, localbundle);
        // 4.添加图片
        FileUtils.copyPatchImgs(FileConstant.FUTURE_DRAWABLE_PATH, FileConstant.DRAWABLE_PATH);
        // 5.删除本次下发的更新文件
        FileUtils.traversalFile(FileConstant.FUTURE_JS_PATCH_LOCAL_FOLDER);
    }


    /**
     * 合并,生成新的bundle文件
     */
    private static void merge(String patcheStr, String bundle) {

        // 3.初始化 dmp
        diff_match_patch dmp = new diff_match_patch();
        // 4.转换pat
        LinkedList<diff_match_patch.Patch> pathes = (LinkedList<diff_match_patch.Patch>) dmp.patch_fromText(patcheStr);
        // 5.pat与bundle合并，生成新的bundle
        Object[] bundleArray = dmp.patch_apply(pathes, bundle);
        // 6.保存新的bundle文件
        try {
            Writer writer = new FileWriter(FileConstant.JS_BUNDLE_LOCAL_PATH);
            String newBundle = (String) bundleArray[0];
            writer.write(newBundle);
            writer.close();
            LogUtils.i("合并文件成功----->4");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
