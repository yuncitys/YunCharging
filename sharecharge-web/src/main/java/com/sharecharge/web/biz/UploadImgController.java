package com.sharecharge.web.biz;

import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.file.FileUtil;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.oss.AliYunUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("sys/uploadImg")
@RequiredArgsConstructor
public class UploadImgController {


    @Value("${sharecharge.profile}")
    private String pathDir;


    @RequestMapping(value = "banner", method = {RequestMethod.GET, RequestMethod.POST})
    @PreAuthorize("@ps.hasPermission(':sys:uploadImg:banner')")
    public ResultUtil uploadImg(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        String prefix = "";
        String dateStr = "";
        String url = "";
        String monthStr = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        //保存上传
        OutputStream out = null;
        InputStream fileInput = null;
        try {
            if (file != null) {
                /*String originalName = file.getOriginalFilename();
                prefix = originalName.substring(originalName.lastIndexOf(".") + 1);
                dateStr = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                String filepath = pathDir + File.separator + dateStr + "_" + monthStr + "." + prefix;
                File files = new File(filepath);
                //打印查看上传路径
                System.out.println(filepath);
                if (!files.getParentFile().exists()) {
                    files.getParentFile().mkdirs();
                }
                try{
                    file.transferTo(files);
                }catch (Exception e){
                    e.printStackTrace();
                }*/
                ResultUtil resultUtil = new ResultUtil();
                url = AliYunUtil.uploadFile(file);
                resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
                resultUtil.setMsg("上传文件成功");
                resultUtil.setData(url);
                return resultUtil;
            }
            return ResultUtil.error("上传文件失败");
        } catch (Exception e) {
            return ResultUtil.error("上传文件失败");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (fileInput != null) {
                    fileInput.close();
                }
            } catch (IOException e) {
            }
        }
//       return ResultUtil.success((File.separator + "sys" + File.separator + "uploadImg" + File.separator + "downLoadBanner" + File.separator + dateStr + "_" + monthStr + "." + prefix).replace("\\", "/"));
    }


    /**
     * 上传升级文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    @PreAuthorize("@ps.hasPermission(':sys:uploadImg:file')")
    @RequestMapping(value = "file", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultUtil file(@RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        String url = "";
        String dateStr = "";
        String monthStr = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        if (file != null) {

            String prefix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

            String filepath = pathDir + File.separator + file.getOriginalFilename();
            if (!prefix.equalsIgnoreCase(".bin")) {
                return ResultUtil.error("请上传bin文件");
            }
            url = AliYunUtil.uploadFile(file);
            Map map = new HashMap<>();
            map.put("url", url);
            map.put("name", file.getOriginalFilename());
            File file1 = FileUtil.multipartFileToFile(file, filepath);
            String md5 = FileUtil.getFileMD5String(file1);
            map.put("length", +file1.length());
            map.put("MD", md5);
            FileUtil.deleteTempFile(file1);
            return ResultUtil.success(map);
        } else {
            return ResultUtil.error("文件为空");
        }

    }

    /**
     * 查看图片
     * @param img
     * @param response
     */
    @RequestMapping(value = "downLoadBanner/{img}", method = {RequestMethod.GET, RequestMethod.POST})
    public void downLoadBanner(@PathVariable("img") String img, HttpServletResponse response) {
        OutputStream os = null;
        try {
            BufferedImage image = ImageIO.read(new FileInputStream(new File(pathDir + File.separator + img)));
            response.setContentType("image/png");
            os = response.getOutputStream();

            if (image != null) {
                ImageIO.write(image, img.substring(img.lastIndexOf(".") + 1), os);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
