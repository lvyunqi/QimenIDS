package com.chuqiyun.ids.utils;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IniUtil {

    /**
     * 读取配置文件int值
     *
     * @param file    文件路径
     * @param section 节点
     * @param key     子节点
     * @return
     * @throws IOException
     */
    public static int getProfileInt(String file, String section, String key) throws IOException {
        String strLine, value = "";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        boolean isInSection = false;
        try {
            while ((strLine = bufferedReader.readLine()) != null) {
                strLine = strLine.trim();
                //strLine = strLine.split("[;]")[0];
                Pattern p;
                Matcher m;
                p = Pattern.compile("\\[\\s*.*\\s*\\]");
                m = p.matcher((strLine));
                if (m.matches()) {
                    p = Pattern.compile("\\[\\s*" + section + "\\s*\\]");
                    m = p.matcher(strLine);
                    if (m.matches()) {
                        isInSection = true;
                    } else {
                        isInSection = false;
                    }
                }
                if (isInSection == true) {
                    strLine = strLine.trim();
                    String[] strArray = strLine.split("=");
                    if (strArray.length == 1) {
                        value = strArray[0].trim();
                        if (value.equalsIgnoreCase(key)) {
                            value = "";
                            return Integer.parseInt(value);
                        }
                    } else if (strArray.length == 2) {
                        value = strArray[0].trim();
                        if (value.equalsIgnoreCase(key)) {
                            value = strArray[1].trim();
                            return Integer.parseInt(value);
                        }
                    } else if (strArray.length > 2) {
                        value = strArray[0].trim();
                        if (value.equalsIgnoreCase(key)) {
                            value = strLine.substring(strLine.indexOf("=") + 1).trim();
                            return Integer.parseInt(value);
                        }
                    }
                }
            }
        } finally {
            bufferedReader.close();
        }
        return 0;
    }

    /**
     * 读取配置文件String值
     *
     * @param file    读取的文件路径
     * @param section 节点
     * @param key     子节点
     * @return
     * @throws IOException
     */
    public static String getProfileString(String file, String section, String key) throws IOException {
        String strLine, value = "";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        boolean isInSection = false;
        try {
            while ((strLine = bufferedReader.readLine()) != null) {
                strLine = strLine.trim();
                //strLine = strLine.split("[;]")[0];
                Pattern p;
                Matcher m;
                p = Pattern.compile("\\[\\s*.*\\s*\\]");
                m = p.matcher((strLine));
                if (m.matches()) {
                    p = Pattern.compile("\\[\\s*" + section + "\\s*\\]");
                    m = p.matcher(strLine);
                    if (m.matches()) {
                        isInSection = true;
                    } else {
                        isInSection = false;
                    }
                }
                if (isInSection == true) {
                    strLine = strLine.trim();
                    String[] strArray = strLine.split("=");
                    if (strArray.length == 1) {
                        value = strArray[0].trim();
                        if (value.equalsIgnoreCase(key)) {
                            value = "";
                            return value;
                        }
                    } else if (strArray.length == 2) {
                        value = strArray[0].trim();
                        if (value.equalsIgnoreCase(key)) {
                            value = strArray[1].trim();
                            return value;
                        }
                    } else if (strArray.length > 2) {
                        value = strArray[0].trim();
                        if (value.equalsIgnoreCase(key)) {
                            value = strLine.substring(strLine.indexOf("=") + 1).trim();
                            return value;
                        }
                    }
                }
            }
        } finally {
            bufferedReader.close();
        }
        return "";
    }

    /**
     * 读取配置文件String值
     *
     * @param file    文件路径
     * @param section 节点
     * @param key     子节点
     * @return
     * @throws IOException
     */
    public static String getProfileString(String file, String section, String key, String defaultValue) throws IOException {
        String strLine, value = "";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        boolean isInSection = false;
        try {
            while ((strLine = bufferedReader.readLine()) != null) {
                strLine = strLine.trim();
                //strLine = strLine.split("[;]")[0];
                Pattern p;
                Matcher m;
                p = Pattern.compile("\\[\\s*.*\\s*\\]");
                m = p.matcher((strLine));
                if (m.matches()) {
                    p = Pattern.compile("\\[\\s*" + section + "\\s*\\]");
                    m = p.matcher(strLine);
                    if (m.matches()) {
                        isInSection = true;
                    } else {
                        isInSection = false;
                    }
                }
                if (isInSection == true) {
                    strLine = strLine.trim();
                    String[] strArray = strLine.split("=");
                    if (strArray.length == 1) {
                        value = strArray[0].trim();
                        if (value.equalsIgnoreCase(key)) {
                            value = "";
                            return value;
                        }
                    } else if (strArray.length == 2) {
                        value = strArray[0].trim();
                        if (value.equalsIgnoreCase(key)) {
                            value = strArray[1].trim();
                            return value;
                        }
                    } else if (strArray.length > 2) {
                        value = strArray[0].trim();
                        if (value.equalsIgnoreCase(key)) {
                            value = strLine.substring(strLine.indexOf("=") + 1).trim();
                            return value;
                        }
                    }
                }
            }
        } finally {
            bufferedReader.close();
        }
        return defaultValue;
    }

    /**
     * 修改ini配置文档中变量的值
     *
     * @param file    配置文档的路径
     * @param section 要修改的变量所在段名称
     * @param key     要修改的变量名称
     * @param value   变量的新值
     * @throws IOException 抛出文档操作可能出现的io异常
     */
    public static boolean setProfileString(String file, String section, String key, String value) throws IOException {
        String fileContent, allLine, strLine, newLine, remarkStr;
        String getValue;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        boolean isInSection = false;
        fileContent = "";
        try {
            while ((allLine = bufferedReader.readLine()) != null) {
                allLine = allLine.trim();
                //System.out.println("allLine == " + allLine);
                // if (allLine.split("[;]").length > 1)
                // remarkStr = ";" + allLine.split(";")[1];
                // else
                // remarkStr = "";
                // strLine = allLine.split(";")[0];
                strLine = allLine;
                Pattern p;
                Matcher m;
                p = Pattern.compile("\\[\\s*.*\\s*\\]");
                m = p.matcher((strLine));
                if (m.matches()) {
                    p = Pattern.compile("\\[\\s*" + section + "\\s*\\]");
                    m = p.matcher(strLine);
                    if (m.matches()) {
                        isInSection = true;
                    } else {
                        isInSection = false;
                    }
                }
                if (isInSection == true) {
                    strLine = strLine.trim();
                    String[] strArray = strLine.split("=");
                    getValue = strArray[0].trim();
                    if (getValue.equalsIgnoreCase(key)) {
                        // newLine = getValue + " = " + value + " " + remarkStr;
                        newLine = getValue + " = " + value + " ";
                        fileContent += newLine + "\r\n";
                        while ((allLine = bufferedReader.readLine()) != null) {
                            fileContent += allLine + "\r\n";
                        }
                        bufferedReader.close();
                        BufferedWriter bufferedWriter =
                                new BufferedWriter(new FileWriter(file, false));
                        bufferedWriter.write(fileContent);
                        bufferedWriter.flush();
                        bufferedWriter.close();

                        return true;
                    }
                }
                fileContent += allLine + "\r\n";
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            bufferedReader.close();
        }
        return false;
    }
}