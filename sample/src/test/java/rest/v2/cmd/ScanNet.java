package rest.v2.cmd;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class ScanNet {
	private static PrintStream out = System.out;

	public static void main(String[] args) throws Exception {

//		scan("10.250.46.131/32");
//		scan("10.250.46.184/32");
//		scan("10.250.46.107/32");

		scanToFile("10.250.43.255/24");
		scanToFile("10.250.44.255/24");
		scanToFile("10.250.45.255/24");
		scanToFile("10.250.46.255/24");
		scanToFile("10.250.47.255/24");
		scanToFile("10.250.48.255/24");
		scanToFile("10.250.49.255/24");
		
	}

	public static void scanToFile(String cidr) throws Exception{
		String name = cidr.replace("/", "-") + ".txt";
		out = new PrintStream(new File(name));
		scan(cidr);
		out.close();

		out = System.out;
	}
	
	public static void scan(String cidr) throws Exception{
		int[] bounds = ScanNet.rangeFromCidr(cidr);
		
		NetworkInterface nci = getNCI("eth3");
		for (int i = bounds[0]; i <= bounds[1]; i++) {
			String address = InetRange.intToIp(i);
			InetAddress ip = InetAddress.getByName(address);
//			InetAddress ip = InetAddress.getByAddress(InetAddress.getByName(address).getAddress());
			
			if (ip.isReachable(nci, 0, 5 * 1000)) { // Try for one tenth of a second
				ip.getHostName();
				out.printf("  Reachable address :: %s/%s -> %s\n", ip.getHostAddress(), toHostName(ip), toMac(ip));
			} else {
				out.printf("Unreachable address :: %s\n", ip.getHostAddress());
			}
		}
		
		out.println();
	}
	
	public static NetworkInterface getNCI(String name) throws UnknownHostException, SocketException {
		NetworkInterface result = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());

		Enumeration<NetworkInterface> a = NetworkInterface.getNetworkInterfaces();
		while(a.hasMoreElements()){
			NetworkInterface nci = a.nextElement();
//			out.format("%s:, virtual=%s, up=%s \n", nci.getName(), nci.getDisplayName(), nci.isVirtual()+"", nci.isUp()+"");

			if(nci.getName().equals(name)){
				result = nci;
			}
		}
		
		return result;
	}
	
	public static String toHostName(InetAddress ip) throws IOException{
		String host = ip.getHostName();

		if(ip.getHostAddress().equals(host)){
			Process p = Runtime.getRuntime().exec("ping -a -n 1 " + ip.getHostAddress());	
			List<String> out = IOUtils.readLines(p.getInputStream(), "MS949");
			
			for(String line : out){
				if(line.startsWith("Ping")){
					host = line.split(" ")[1];
				}
			}
		}
		
		return host;
	}
	
	public static String toMac(InetAddress ip) {
		try {
//			ip = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);

			if(network == null){
				String mac = toMacFromArp(ip);
				return mac == null ? "<unknown>" : mac;
			}
			
			byte[] mac = network.getHardwareAddress();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static String toMacFromArp(InetAddress ip) throws IOException{
		Process p = Runtime.getRuntime().exec("arp -a " + ip.getHostAddress());	
		List<String> out = IOUtils.readLines(p.getInputStream(), "MS949");
		
		for(String line : out){
			line = line.trim();
			if(line.startsWith(ip.getHostAddress())){
				return line.split("\\s+")[1];
			}
		}
		
		return null;
	}
	
	public static void toMacWithJcap(){
//		 https://sourceforge.net/projects/jpcap/files/latest/download
	}

	public static int[] rangeFromCidr(String cidrIp) {
		int maskStub = 1 << 31;
		String[] atoms = cidrIp.split("/");
		int mask = Integer.parseInt(atoms[1]);

		int[] result = new int[2];
		result[0] = InetRange.ipToInt(atoms[0]) & (maskStub >> (mask - 1)); // lower bound
		result[1] = InetRange.ipToInt(atoms[0]); // upper bound

		out.format("%d/[%s-%s]\n", mask, InetRange.intToIp(result[0]), InetRange.intToIp(result[1]));

		return result;
	}

	static class InetRange {
		public static int ipToInt(String ipAddress) {
			try {
				byte[] bytes = InetAddress.getByName(ipAddress).getAddress();
				int octet1 = (bytes[0] & 0xFF) << 24;
				int octet2 = (bytes[1] & 0xFF) << 16;
				int octet3 = (bytes[2] & 0xFF) << 8;
				int octet4 = bytes[3] & 0xFF;
				int address = octet1 | octet2 | octet3 | octet4;

				return address;
			} catch (Exception e) {
				e.printStackTrace();

				return 0;
			}
		}

		public static String intToIp(int ipAddress) {
			int octet1 = (ipAddress & 0xFF000000) >>> 24;
			int octet2 = (ipAddress & 0xFF0000) >>> 16;
			int octet3 = (ipAddress & 0xFF00) >>> 8;
			int octet4 = ipAddress & 0xFF;

			return new StringBuffer().append(octet1).append('.').append(octet2).append('.').append(octet3).append('.')
					.append(octet4).toString();
		}
	}
}