package com.kh.arori.controller.study;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.arori.entity.MemberDto;
import com.kh.arori.entity.study.ClassesDto;
import com.kh.arori.repository.study.ClassesDao;
import com.kh.arori.service.study.ClassesService;

@Controller
public class ClassesController {

	@Autowired
	private ClassesService classesService;
	
	@Autowired
	private ClassesDao classesDao;

	// 나의 클래스 
	@GetMapping("/classes/myclass")
	public String myclass() {
		return "classes/myclass";
	}

	@GetMapping("/classes/create")
	public String create() {
		return "classes/create";
	}

	// 클래스 생성
	@PostMapping("/classes/create")
	public String create(@ModelAttribute ClassesDto classesDto, HttpSession session) {
		// 세션(userinfo) 를 MemberDto 로 받아온다.
		MemberDto memberDto = (MemberDto) session.getAttribute("userinfo");
		classesDto.setMember_no(memberDto.getMember_no());
		int c_no = classesService.createClasses(classesDto);
		return "redirect:detail/" + c_no; // == http:/localhost:8080/arori/classes/classDetail/클래스 번호
	}

	// 내가 만든 클래스 디테일 페이지
	@GetMapping("/classes/detail/{c_no}")
	public String detail(@PathVariable int c_no, Model model, HttpSession session) {
		// 매개변수로 받아온 클래스 번호에 대한 디비 정보를 dao 혹은 service 를 통해서 받아온다.(classesDto 단일조회)
		// 받아온 classesDto 를 model 로 보낸다.
		model.addAttribute("c_no", c_no);
		// 클래스 넘버를 이용한 단일조회
		ClassesDto classesDto = classesDao.get(c_no);
		model.addAttribute("classesDto", classesDto);
		
		return "classes/detail";
	}
	
//	// 클래스 수정
//	@GetMapping("/classes/edit/{c_no}")
//	public String edit(@PathVariable int c_no, RedirectAttributes attr, Model model, ModelAttribute classesDto) {
//		// 정보수정
//		classesDao.edit(classesDto);
//
//		// 수정한 정보 단일 조회
//		ClassesDto classesDto = classesDao.get(c_no);
//		model.addAttribute("classesDto", classesDto);
//		attr.addAttribute("c_no",c_no);
//
//		return "classes/detail";
//	}
//				
	@GetMapping("/classes/edit/{c_no}")//get매핑일때는 정보 필요없엉
	   public String edit(@PathVariable int c_no, RedirectAttributes attr, Model model) {
		
	   //받아온 c_no로 정보를 조회후 classesDto에 저장
	      ClassesDto classesDto = classesDao.get(c_no);
	      model.addAttribute("classesDto", classesDto);
	      attr.addAttribute("c_no",c_no);

	      return "classes/edit";
	   }

	@PostMapping("/classes/edit")//post매핑일때 정보수정
	   public String edit(@PathVariable int c_no, RedirectAttributes attr,@ModelAttribute ClassesDto classesDto) {

	      // 정보수정
	      classesDao.edit(classesDto);
	      attr.addAttribute("c_no",c_no);

	      return "classes/detail"+ c_no;
	   }



} 
