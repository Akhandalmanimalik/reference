package com.nuvizz.emp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nuvizz.emp.dao.UserDAOI;
import com.nuvizz.emp.model.State;

@Controller
@RequestMapping("/state.spring")
public class RegistrationController {

	private UserDAOI udao;

	@RequestMapping(value = "/getcities", method = RequestMethod.POST)
	public @ResponseBody
	List getAllRelatedCity(HttpServletRequest request, Model uiModel)
			throws Exception {

		String statename = request.getParameter("country_id");
		System.out.println(statename + "In controller");
		State st = new State();
		st.setStateName("Odisha");
		ArrayList al = (ArrayList) udao.getCities(st);
		return al;
	}

	public void setUserDAOI(UserDAOI udao) {
		this.udao = udao;
	}
}