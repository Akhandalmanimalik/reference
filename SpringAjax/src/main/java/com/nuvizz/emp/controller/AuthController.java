package com.nuvizz.emp.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nuvizz.emp.dao.UserDAOI;
import com.nuvizz.emp.model.EmpDetails;
import com.nuvizz.emp.model.State;
import com.nuvizz.emp.model.User;

@Controller
@RequestMapping("/root")
public class AuthController {

	@Autowired
	UserDAOI udao;

	public void setUserDAOI(UserDAOI udao) {
		this.udao = udao;

	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(@ModelAttribute("user") User user,
			BindingResult result) {
		try {
			boolean flag = udao.checkUser((User) user);

			return new ModelAndView("/userhome");
		} catch (Exception e) {
			return new ModelAndView("/invaliduser");
		}
	}

	@RequestMapping(value = "/addEmployee", method = RequestMethod.POST)
	public ModelAndView addEmployee(
			@ModelAttribute("modelAttributeName") EmpDetails empdetail,
			BindingResult result) {
		try {
			int i = udao.addEmployee((EmpDetails) empdetail);
			return new ModelAndView("/registrationsuccess");
		} catch (Exception e) {
			return new ModelAndView("/registrationfailure");
		}
	}

	@RequestMapping(value = "/getCities", method = RequestMethod.GET)
	public @ResponseBody List<String> getCity(
			@RequestParam(value = "state", defaultValue = "Odisha") String state) {
		State st = new State();
		st.setStateName(state);
		List<String> cities = (ArrayList<String>) udao.getCities(st);
		System.out.println(cities);
		List<String> cities=new ArrayList<String>();

		return cities;
	}
}
