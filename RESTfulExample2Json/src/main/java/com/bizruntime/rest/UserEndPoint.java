package com.bizruntime.rest;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.bizruntime.dao.UserDAOI;
import com.bizruntime.factory.DAOFactory;
import com.bizruntime.vo.User;

@Path("/userend")
public class UserEndPoint {
	UserDAOI userDao= DAOFactory.getUserDAO();
	private static final String SUCCESS_RESULT = "<result>success</result>";
	private static final String FAILURE_RESULT = "<result>failure</result>";
	/*
	 * method=get,content-type=application/json,view=request,
	 * url=http://localhost:8080/RESTfulExample/rest/userend/101
	 * body=nothing
	 * */
	@GET
	@Path("{param}")
//	@Produces("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser(@PathParam("param") int userid) {
		String username = "Hello " + userDao.getUserId(userid);
		User user=new User();
		user.setUserId(userid);
		user.setUserName(userDao.getUserId(userid));
		return user;
	}

	// url=http://localhost:8080/RESTfulExample/rest/userend/userid/5,method=delete,
	// request=request,content-type=nothing
	@DELETE
	@Path("/userid/{userid}")
	@Produces(MediaType.APPLICATION_XML)
	public String deleteUser(@PathParam("userid") int userid) {
		boolean result = userDao.deleteUser(userid);
		if (result == true) {
			return SUCCESS_RESULT;
		}
		return FAILURE_RESULT;
	}

	/*
	 * view=request,content-type=application/json,method=put,
	 * url=http://localhost:8080/RESTfulExample/rest/userend/create, body=
	 * {"userId":210,"userName":"Omm"}
	 */
	//insert record
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/create")
	public String updateUser(User user) throws IOException {
		int result = userDao.createUser(user);
		if (result == 1) {
			return SUCCESS_RESULT;
		}
		return FAILURE_RESULT;
	}
	/*view=request,content-type=application/json,method=post,
	body={"userId":210,"userName":"Omm Namah Sibaya"}*/
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/update")
	public String createUser(User user) throws IOException {
		int result = userDao.updateUserName(user);
		if (result == 1) {
			return SUCCESS_RESULT;
		}
		return FAILURE_RESULT;
	}

	/*
	 * @GET
	 * 
	 * @Path("{param}") public Response getUser(@PathParam("param") int userid)
	 * { UserDAOI udao = DAOFactory.getUserDAO(); String username = "Hello " +
	 * udao.getUserId(userid); return
	 * Response.status(200).entity(username).build(); }
	 */
	/*@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/getusers")
	public List<User> getUsers() {
		return userDao.getAllUsers();
	}*/
	/*
	 * @GET
	 * 
	 * @Path("{param}")
	 * 
	 * @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	 * public List getUserList(@PathParam("param") int userid) { UserDAOI udao =
	 * DAOFactory.getUserDAO(); String username = "Hello " +
	 * udao.getUserId(userid); System.out.println("list"); List
	 * userDetailsList=new ArrayList(); userDetailsList.add("ram");
	 * userDetailsList.add("sita"); userDetailsList.add("laxman"); return
	 * userDetailsList; }
	 */

}
