package com.sep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.apphosting.utils.config.AppYaml.StaticFile;
import com.socio.recomgift.model.EventInviteeDO;
import com.socio.recomgift.model.Friend;
import com.socio.recomgift.model.FriendInterest;
import com.socio.recomgift.model.FriendLike;

/**
 * Servlet implementation class RecommendGiftHandler
 */

public class RecommendGiftHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendGiftHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// Process FB response, if it is HTTP GET
		if(request.getParameter("method") != null && "getFriendId".equals(request.getParameter("method"))) {
			getFriendId(request, response);
		}
		processResponse(request, response);
	}
	
	
	
	private void getFriendId(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter servletOutput = response.getWriter();
		String friendId = request.getParameter("friendId");
		HttpSession session = request.getSession(true);
//		servletOutput.println("friendId: "+ friendId);
//		servletOutput.println("<br/>");
		String token = "CAAHDi8lqRfABACLVkue4UIx1vbZB0T0RC1bVrf4knhahc5LrtiFZBPs8fQ92DARqeFeZAiEQiUm285RIBytqtGWYpQx0dHzPxOiMRVhz2FrHMmIfWh5ZB7kmhOSgOsQ59wOZA2pcuulU7OFPZBPZAmA";
		String likesAPICall = "https://graph.facebook.com/220900111?fields=likes&access_token=" + token;		            	
    	URL url2 = new URL(likesAPICall);
		HttpURLConnection httpConn2 = (HttpURLConnection) url2.openConnection();
		
		httpConn2.setRequestMethod("GET");
		httpConn2.connect();
		BufferedReader buffRdr2 = new BufferedReader(new InputStreamReader(httpConn2.getInputStream()));

		String line2 = "";
		String respLine2 = "";
		while (null != (line2 = buffRdr2.readLine())){
			respLine2 = respLine2 + line2;
		}
		JSONObject likes = new JSONObject(respLine2);
		JSONObject likesObject = null;
		JSONArray likesArray = null; 
		if(likes.has("likes")) {
		likesObject = (JSONObject) likes.get("likes");
		if(likesObject != null) {
			likesArray = (JSONArray) likesObject.get("data");
			}
		}
		try {
			session.setAttribute("likes", likesArray.toString());
			
		}
		catch(Exception e) {
			e.printStackTrace(servletOutput);
		}
		
		buffRdr2.close();
		String redirect = response.encodeRedirectURL(request.getContextPath() + "./recommendGift/FriendsLikes.jsp" );
		response.sendRedirect(redirect);
		
		
	}

	protected void processResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{/*
		PrintWriter    servletOutput = response.getWriter(); 
		//servletOutput.print("<html><body><h1>The Facebook Friends are: </h1><p />");
		Friend friend = null;
		FriendLike friendLike = null;
		FriendInterest friendInterest = null;
		List<Friend> friendList = new ArrayList<Friend>();
		List<FriendLike> friendLikes = new ArrayList<FriendLike>();
		List<FriendInterest> friendInterestList = new ArrayList<FriendInterest>();
		HttpSession session = request.getSession(true);
		String userId = (String) session.getAttribute("USER_ID");
		userId = userId == null ? "12345" : userId.trim();		
		
		// Retrieve the State and auth code FB response 
		String stStr = request.getParameter("state");
		stStr = stStr == null ? "" : stStr;
		
		String authCd = request.getParameter("code");
		authCd = authCd == null ? "" : authCd;
		
		// Exchange Auth token for Access Token
		String fbUrl = "https://graph.facebook.com/oauth/access_token?client_id=496480123766256&redirect_uri=http://soceveplnr.appspot.com/RecommendGiftHandler&client_secret=1887e3c3807cdcb5a2c81de6ef4feaed&code="+authCd;
		Vector<String> respVec = submitHTTPRequest(fbUrl);
		Enumeration<String> respEnum = respVec.elements();
		while(respEnum.hasMoreElements()){
			String str = respEnum.nextElement();
			str = str == null ? "" : str;
			if(str.indexOf("access_token") != -1 && str.indexOf("expires") != -1) {
				StringTokenizer sToken = new StringTokenizer(str, "&");
				String aToken = sToken.nextToken();
				String expStr = sToken.nextToken();
				StringTokenizer accToken = new StringTokenizer(aToken, "=");
				String acTok1 = accToken.nextToken();
				String acTok2 = accToken.nextToken();
				String fbInsToken = "https://graph.facebook.com/debug_token?input_token=" + acTok2 + "&access_token=" + acTok2;
				Vector<String> respVec1 = submitHTTPRequest(fbInsToken);
				//servletOutput.println("Vector : " + respVec1);
				String jsonStr = respVec1.elementAt(0);
				String fbAPICall = "https://graph.facebook.com/me?fields=id,name,friends&access_token=" + acTok2;
				try {
					URL url = new URL(fbAPICall);
					HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
					
					httpConn.setRequestMethod("GET");
					httpConn.connect();
					
					// reads the FB response and retrieve the friends list
					BufferedReader buffRdr = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			
					String line = "";
					String respLine = "";
					while (null != (line = buffRdr.readLine())){
						respLine = respLine + line;
					}
					JSONObject frnd = new JSONObject(respLine);
			
					JSONObject j2 = (JSONObject) frnd.get("friends");
					JSONArray j3 = (JSONArray) j2.get("data");
					int numberOfFriends = j3.length();
					for(int i=0;i<10;i++) {
						//servletOutput.println(i);
						//servletOutput.println("<br/>");
		            	friend = new Friend();
		            	//servletOutput.println("j3.getJSONObject(i).getString(id): "+j3.getJSONObject(i).getString("id"));
						servletOutput.println("<br/>");
		            	friend.setFriendId(j3.getJSONObject(i).getString("id"));
		            	friend.setFriendName(j3.getJSONObject(i).getString("name"));
		            	String likesAPICall = "https://graph.facebook.com/"+j3.getJSONObject(i).getString("id")+"?fields=likes&access_token=" + acTok2;		            	
//		            	servletOutput.print(likesAPICall);
		            	URL url2 = new URL(likesAPICall);
						HttpURLConnection httpConn2 = (HttpURLConnection) url2.openConnection();
						
						httpConn2.setRequestMethod("GET");
						httpConn2.connect();
						
						// reads the FB response and retrieve the friends list
						BufferedReader buffRdr2 = new BufferedReader(new InputStreamReader(httpConn2.getInputStream()));
				
						String line2 = "";
						String respLine2 = "";
						while (null != (line2 = buffRdr2.readLine())){
							respLine2 = respLine2 + line2;
						}
						JSONObject likes = new JSONObject(respLine2);
						JSONObject likesObject = null;
						JSONArray likesArray = null; 
//						servletOutput.println("respLine2 " + respLine2);
//						servletOutput.println("<br/>");
//						servletOutput.println("likes " + likes);
//						servletOutput.println("<br/>");
//						servletOutput.println("likes Has Likes" + likes.has("likes"));
//						servletOutput.println("<br/>");
						if(likes.has("likes")) {
						likesObject = (JSONObject) likes.get("likes");
						
						
//						servletOutput.println("likesObject " + likesObject);
//						servletOutput.println("<br/>");
//						servletOutput.println("likes " + likes.toString());
//						servletOutput.println("<br/>");
						if(likesObject != null) {
						likesArray = (JSONArray) likesObject.get("data");
						}
						
//						servletOutput.println("likesArray " + likesArray);
//						servletOutput.println("<br/>");
//						servletOutput.println("likesArray " + likesArray.toString());
//						servletOutput.println("<br/>");
						
						
						for(int k=0;k<likesArray.length();k++) {
//							servletOutput.print("likesArray.length: "+likesArray.length());
//							servletOutput.println("<br/>");
//							servletOutput.print("k looping "+k);
//							servletOutput.println("<br/>");
			            	if (likesArray.getJSONObject(k) != null) {
			            	friendLike = new FriendLike();	
			            	friendLike.setLikeCategory(likesArray.getJSONObject(k).getString("category"));
			            	friendLike.setLikeName(likesArray.getJSONObject(k).getString("category"));
//			            	servletOutput.println("<br/>");
//			            	servletOutput.println(likesArray.getJSONObject(k).getString("category"));
			            	friendLike.setLikeName(likesArray.getJSONObject(k).getString("name"));
//			            	servletOutput.println("<br/>");
//			            	servletOutput.println(likesArray.getJSONObject(k).getString("name"));
//			            	servletOutput.println("<br/>");
			            	friendLike.setLikeId(likesArray.getJSONObject(k).getString("id"));
			            	friendLikes.add(friendLike);
			            	friend.setFriendLikes(friendLikes);
			            	}
						}
					}
						buffRdr2.close();
						String interestsAPICall = "https://graph.facebook.com/"+j3.getJSONObject(i).getString("id")+"?fields=interests&access_token=" + acTok2;
		            	URL url3 = new URL(interestsAPICall);
						HttpURLConnection httpConn3 = (HttpURLConnection) url3.openConnection();
						
						httpConn3.setRequestMethod("GET");
						httpConn3.connect();
						
						// reads the FB response and retrieve the friends list
						BufferedReader buffRdr3 = new BufferedReader(new InputStreamReader(httpConn3.getInputStream()));
				
						String line3 = "";
						String respLine3 = "";
						while (null != (line3 = buffRdr3.readLine())){
							respLine3 = respLine3 + line3;
						}
						JSONObject interests = new JSONObject(respLine3);
						JSONObject interestsObject = null;
						JSONArray interestsArray = null; 
						if(interests.has("interests")) {
							interestsObject = (JSONObject) interests.get("interests");
						
							if(interestsObject != null) {
							interestsArray = (JSONArray) interestsObject.get("data");
						}
						for(int k=0;k<interestsArray.length();k++) {
			            	if (interestsArray.getJSONObject(k) != null) {
			            	friendInterest = new FriendInterest();	
			            	friendInterest.setInterestCategory(likesArray.getJSONObject(k).getString("category"));
			            	friendInterest.setInterestName(likesArray.getJSONObject(k).getString("name"));
			            	friendInterest.setInterestId(likesArray.getJSONObject(k).getString("id"));
			            	friendInterestList.add(friendInterest);
			            	friend.setFriendInterestsList(friendInterestList);
			            	}
						}
					}
							buffRdr3.close();
							friendList.add(friend);
				}
				
//---------------------------------------------------------------------------------------------------------------------------
					String likesAPICall = "https://graph.facebook.com/220900111?fields=likes&access_token=" + acTok2;		            	
	            	URL url2 = new URL(likesAPICall);
					HttpURLConnection httpConn2 = (HttpURLConnection) url2.openConnection();
					
					httpConn2.setRequestMethod("GET");
					httpConn2.connect();
					BufferedReader buffRdr2 = new BufferedReader(new InputStreamReader(httpConn2.getInputStream()));
			
					String line2 = "";
					String respLine2 = "";
					while (null != (line2 = buffRdr2.readLine())){
						respLine2 = respLine2 + line2;
					}
					JSONObject likes = new JSONObject(respLine2);
					JSONObject likesObject = null;
					JSONArray likesArray = null; 
					if(likes.has("likes")) {
					likesObject = (JSONObject) likes.get("likes");
					if(likesObject != null) {
						likesArray = (JSONArray) likesObject.get("data");
						}
					}
//---------------------------------------------------------------------------------------------------------------------------					
					
					
					
					try {
						session.setAttribute("likes", likesArray.toString());
						
					}
					catch(Exception e) {
						e.printStackTrace(servletOutput);
					}
					
					buffRdr.close();
					String redirect = response.encodeRedirectURL(request.getContextPath() + "./recommendGift/FriendsLikes.jsp" );
					response.sendRedirect(redirect);
					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				finally {
					
				}
			}
		}
		
		//servletOutput.print("</body></html>");
		//servletOutput.close();
	*/}

	
	// This method submits HTTP request for the given URL and return the response as vector Strings
	protected Vector<String> submitHTTPRequest(String urlStr) 
	{
		Vector<String> resp = new Vector<String>();
		try {
			
			URL url = new URL(urlStr);
			URLConnection urlConn = url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConn.connect();
			
			// reads the FB response and will be used for the API calls
			BufferedReader buffRdr = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
	
			String line = null;
			while (null != (line = buffRdr.readLine())){
			    resp.add(line);
			}
			buffRdr.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			
		}
		return resp;
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
//				String fbUrl = "https://www.facebook.com/dialog/oauth?client_id=496480123766256&redirect_uri=http://soceveplnr.appspot.com/RecommendGiftHandler&state=FBResponse&scope=email, read_friendlists, friends_about_me, user_about_me, user_likes, friends_likes, friends_interests, user_interests";
//				response.sendRedirect(fbUrl);
				HttpSession session = request.getSession(true);
				ArrayList<EventInviteeDO> eventAndInviteeList = new ArrayList<EventInviteeDO>();
				eventAndInviteeList = retrieveEventsAndFriends(request, response);
				PrintWriter servletOutput = response.getWriter();
				servletOutput.println("eventAndInviteeList.size(): "+eventAndInviteeList.size());
				servletOutput.println("<br/>");
				session.setAttribute("eventAndInviteeList", eventAndInviteeList);
				String redirect = response.encodeRedirectURL(request.getContextPath() + "./recommendGift/EventsAndInviteesDisplay.jsp" );
				response.sendRedirect(redirect);
				processResponse(request, response);
			
	}

	private ArrayList<EventInviteeDO> retrieveEventsAndFriends(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logger logger = Logger.getLogger(RecommendGiftHandler.class);
		ArrayList<EventInviteeDO> eventAndInviteeList = null;
		EventInviteeDO eventInviteeDO = null;
		Connection conn = null;
		Statement stmt =  null;
		PrintWriter servletOutput = response.getWriter();
		
		try {
			SEP_DB_Manager dbMgr = new SEP_DB_Manager();
			conn = SEP_DB_Manager.getConnection();
			String queryForEventsAndInvitees = "select i.INVITEE_email_id, i.INVITEE_NAME, i.EVENT_ID, e.EVENT_NAME from INVITEE_LIST i, EVENT_MASTER e, USER_MASTER u where u.ROW_ID = e.USER_ID and e.ROW_ID = i.EVENT_ID";
			logger.info("Query"+queryForEventsAndInvitees);
		    stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery(queryForEventsAndInvitees);
		    eventAndInviteeList = new ArrayList<EventInviteeDO>();
		    while(rs.next()) {
		    	logger.info("inside rs.next()");
		    	eventInviteeDO = new EventInviteeDO();
		    	String inviteeId = rs.getString("INVITEE_EMAIL_ID");
		    	logger.info("inviteeId: "+inviteeId);
		    	servletOutput.println("inviteeId: "+inviteeId);
				servletOutput.println("<br/>");
		    	eventInviteeDO.setInviteeId(inviteeId);
		    	String eventId = rs.getString("EVENT_ID");
		    	eventInviteeDO.setEventId(eventId);
		    	String inviteeName = rs.getString("INVITEE_NAME");
		    	eventInviteeDO.setInviteeName(inviteeName);
		    	String eventName = rs.getString("EVENT_NAME");
		    	eventInviteeDO.setEventName(eventName);
		    	eventAndInviteeList.add(eventInviteeDO);
		    } 
		
			stmt.close();
			conn.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally {

		}
		return eventAndInviteeList;
	}
}
