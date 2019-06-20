package com.heshun.modbus.entity.driver;

import com.heshun.modbus.common.Config;
import com.heshun.modbus.util.ELog;
import com.heshun.modbus.util.Utils;
import com.sun.istack.internal.NotNull;
import org.apache.http.util.TextUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class DriverLoader {

    private static ConcurrentHashMap<String, DeviceDriver> mDriverContainer = new ConcurrentHashMap<>();

    private static int[] decodeKey = new int[]{5, 8, 7, 2};

    public static void main(String[] a) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        OutputStreamWriter fileWriter = null;
        try {
            File hiddenDir = new File("src/main/resource/dri/bk");
            if (!hiddenDir.exists())
                return;
            String[] configs = hiddenDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".cfg");
                }
            });
            for (String c : Objects.requireNonNull(configs)) {
                File f = new File(hiddenDir, c);
                fis = new FileInputStream(f);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                String text = new String(buffer);
                String _1 = encoder(text);
                StringBuilder sb = new StringBuilder(_1);
                sb.insert(0, randomString(decodeKey[0]));
                sb.insert(sb.length() - decodeKey[3], randomString(decodeKey[1]));
//
                String _2 = encoder(sb.toString());
                StringBuilder result = new StringBuilder(_2);

                result.insert(0, randomString(decodeKey[2]));
                fos = new FileOutputStream(new File("src/main/resource/dri", c.replace(".cfg", ".dr")));
                fileWriter = new OutputStreamWriter(fos);
                fileWriter.write(result.toString());
                fileWriter.flush();

            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

    }

    public static DeviceDriver load(@NotNull String _temp) {
        String fileName = _temp.toLowerCase();
        if (mDriverContainer.containsKey(fileName))
            return mDriverContainer.get(fileName);


        FileReader fr = null;
        BufferedReader br = null;
        File configFile = null;
        try {
            decrypt(fileName);
            configFile = Utils.getConfigFile(String.format("/dri/%s.tmp", fileName));
            fr = new FileReader(configFile);
            br = new BufferedReader(fr);
            DeviceDriver driver = new DeviceDriver();
            driver.setName(_temp);
            for (; ; ) {
                String stream = br.readLine();
                if (stream == null) break;
                String line = stream.trim();
                if (line.startsWith("#") || line.startsWith("//") || TextUtils.isEmpty(line)) continue;
                    //注释，do nothing
                else if (line.startsWith("[")) {
                    if (line.endsWith("]")) {
                        String mask = line.substring(1, line.length() - 1);
                        if (!TextUtils.isEmpty(mask))
                            driver.setMask(mask.trim());
                    }
                } else {
                    DriverItem item = new DriverItem(line);
                    driver.register(item);
                }
            }
            if (driver.size() > 0) {
                mDriverContainer.put(fileName, driver);
                return driver;
            } else ELog.getInstance().err(String.format("驱动文件[%s.dr]解析异常", fileName));
        } catch (FileNotFoundException e) {
            ELog.getInstance().err(String.format("未找到驱动文件[%s.dr]，尝试使用兼容解包策略", fileName));

        } catch (IOException e) {
            ELog.getInstance().err(String.format("加载驱动失败，请检查驱动文件[%s.dr]是否正常", fileName));

        } catch (IllegalStateException e) {
            ELog.getInstance().err(String.format("驱动文件[%s.dr]格式异常，请检查", fileName));

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (configFile != null && configFile.exists() && !Config.isDebug) configFile.delete();

        }

        return null;
    }

    private static void decrypt(String oFileName) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        OutputStreamWriter fileWriter = null;

        try {
            fis = new FileInputStream(Utils.getConfigFile(String.format("/dri/%s.dr", oFileName)));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            String origin = new String(buffer);

            String code = URLDecoder.decode(decoder(origin.substring(decodeKey[2])), "UTF-8");
            if (code.length() <= 8)
                throw new IllegalStateException();
            String result = decoder(code.substring(decodeKey[0], code.length() - decodeKey[1] - decodeKey[3]).concat(code.substring(code.length() - decodeKey[3])));
            File target = Utils.getConfigFile(String.format("/dri/%s.tmp", oFileName));

            fos = new FileOutputStream(target);

            fileWriter = new OutputStreamWriter(fos);
            fileWriter.write(URLDecoder.decode(result, "UTF-8"));
            fileWriter.flush();

        } finally {
            if (fis != null)
                fis.close();

            if (fos != null)
                fos.close();
            if (fileWriter != null)
                fileWriter.close();
        }

    }

    private static String decoder(String origin) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return new String(decoder.decodeBuffer(origin));
    }

    private static String encoder(String origin) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(origin.getBytes()).replaceAll("[\r\n]", "");
    }

    private static String randomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number;
            if (i == 0)
                number = random.nextInt(26) + 26;
            else
                number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
