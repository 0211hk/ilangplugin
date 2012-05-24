package org.hk.io.language.plugin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class SystemExecUtil {

    public static final String[] execCommand(String[] cmds) throws IOException, InterruptedException {
        String[] returns = new String[3];
        Runtime r = Runtime.getRuntime();
        Process p = r.exec(cmds);
        InputStream in = null;
        BufferedReader br = null;
        try {
            in = p.getInputStream();
            StringBuffer out = new StringBuffer();
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                out.append(line + SystemPropertyUtil.getLineSeparator());
            }
            returns[0] = out.toString();
            br.close();
            in.close();
            in = p.getErrorStream();
            StringBuffer err = new StringBuffer();
            br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null) {
                err.append(line + SystemPropertyUtil.getLineSeparator());
            }
            returns[1] = err.toString();
            returns[2] = Integer.toString(p.waitFor());
            return returns;
        } finally {
            if (br != null) {
                br.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }
}
