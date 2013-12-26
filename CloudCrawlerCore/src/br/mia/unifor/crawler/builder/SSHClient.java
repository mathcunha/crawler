package br.mia.unifor.crawler.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jclouds.cloudstack.functions.ReuseOrAssociateNewPublicIPAddress;
import org.jclouds.compute.domain.ExecChannel;

import br.mia.unifor.crawler.executer.artifact.Scriptlet;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SSHClient {

	private static Logger logger = Logger.getLogger(SSHClient.class.getName());

	public static String exec(Scriptlet commands, String user, String host,
			String credential, Boolean password) {
		StringBuffer result = new StringBuffer();
		for (String command : commands.getScripts()) {
			String strResult = exec(command, user, host, credential, password);
			if (strResult != null){
				result = result.append(strResult);
			}else{
				return null;
			}
		}

		return result.toString();
	}

	private static void readAll(BufferedReader bufferedReader,
			StringBuffer strBuff) throws IOException{
		char[] charBuffer = new char[4096];
		int count = 0;

		do {
			logger.info("reading the buffer");
			count = bufferedReader.read(charBuffer, 0, charBuffer.length);
			logger.info(count+" chars will be appended");
			if (count >= 0)
				strBuff.append(charBuffer, 0, count);
		} while (count > 0);
		bufferedReader.close();
	}

	public static String exec(String command, String user, String host,
			String credential, Boolean password) {

		JSch jsch = new JSch();

		Session session = null;
		ChannelExec channel = null;
		boolean connected = true;
		try {
			session = jsch.getSession(user, host, 22);

			// username and password will be given via UserInfo interface.
			UserInfo ui = new MyUserInfo(credential, password);
			session.setUserInfo(ui);

			if (ui.getPassphrase() != null) {
				jsch.addIdentity(ui.getPassphrase());
			}

			session.connect();

			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setInputStream(null);
			InputStream stdout = channel.getInputStream();
			InputStream stderr = channel.getErrStream();
			channel.connect();

			while (!channel.isClosed()) {
				Thread.sleep(1000);
			}

			logger.info("channel closed");
			StringBuffer result = new StringBuffer();
			if (channel.getExitStatus() != 0) {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(stderr));
				readAll(bufferedReader, result);

				throw new Exception(result.toString());
			} else {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(stdout));
				readAll(bufferedReader, result);
				String value = result.toString();
				logger.info("ssh return is " + value);
				return value;
			}
			

		} catch (Exception e) {
			logger.log(Level.SEVERE, "error executing remote script", e);
		} finally {

			logger.info("disconnecting session");
			session.disconnect();
			logger.info("disconnected");

			if (false) {
				logger.info("disconnecting channel");
				channel.disconnect();
			}

		}

		return null;
	}

	public static class MyUserInfo implements UserInfo {
		private final String credential;
		private final Boolean password;

		public MyUserInfo(String credential, Boolean password) {
			this.credential = credential;
			this.password = password;
		}

		public String getPassphrase() {
			if (password) {
				return null;
			} else {
				return credential;
			}
		}

		public String getPassword() {
			if (password) {
				return credential;
			} else {
				return null;
			}

		}

		public boolean promptPassphrase(String arg0) {
			return true;
		}

		public boolean promptPassword(String arg0) {
			return true;
		}

		public boolean promptYesNo(String arg0) {
			return true;
		}

		public void showMessage(String arg0) {
			logger.info(arg0);
		}

	}

	public static void main(String[] args) {
		System.out.println(SSHClient.exec("~/executeSpec.sh 1 1364334910455",
				args[0], args[1], args[2], false));
	}
}
