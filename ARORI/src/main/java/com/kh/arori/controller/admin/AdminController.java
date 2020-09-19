package com.kh.arori.controller.admin;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.arori.entity.admin.ChartDto;
import com.kh.arori.entity.img.This_imgDto;
import com.kh.arori.entity.member.AllMemberDto;
import com.kh.arori.entity.member.AroriMemberDto;
import com.kh.arori.entity.member.MemberDto;
import com.kh.arori.entity.member.ReportDto;
import com.kh.arori.entity.study.ClassesDto;
import com.kh.arori.repository.admin.AdminDao;
import com.kh.arori.repository.member.MemberDao;
import com.kh.arori.service.admin.AdminService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private AdminService adminService;

	@Autowired
	private SqlSession sqlSession;

	@Autowired
	private AdminDao adminDao;

	@GetMapping("/main")
	public String adminPage(Model model) {

		// 오늘의 아로리
		int[] count = adminService.todayCount();
		model.addAttribute("count", count);

		// 현재 날짜
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -2);
		String today = format.format(cal.getTime());
		
		// 회원 통계
		// 현재로부터 3개월 간의 통계
		ChartDto member = ChartDto.builder().table_name("member").col("member_join").period(today).build();

		List<ChartDto> memberChart = adminDao.thisChart(member);
		
		String[] memberWhen = new String[memberChart.size()];
		String[] memberCount = new String[memberChart.size()];
		
		Map<String, String[]> memberMap = new HashMap<String, String[]>();
		System.out.println(memberChart.size());
		for (int i = 0; i < memberChart.size(); i++) {
			memberWhen[i] =memberChart.get(i).getWhen();
			memberCount[i] = String.valueOf(memberChart.get(i).getCount());
		}
		
		memberMap.put("when", memberWhen);
		memberMap.put("count", memberCount);
		
		model.addAttribute("memberMap", memberMap);
		
		// 클래스 통계
		// 현재로부터 3개월 간의 통계

		return "admin/main_admin";
	}

	@GetMapping("/memberList")
	public String memberList() {
		return "admin/memberList";
	}

	// 날짜 변수 타입 매핑
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		// 관리자가 정지일자 입력할때 년,월,일만 작성해도 가능하도록 설정
	}

	// 리스트 연습
	@GetMapping("/allList")
	public String allList(Model model, @ModelAttribute AllMemberDto allMemberDto, @ModelAttribute ClassesDto classesDto,
			@ModelAttribute MemberDto memberDto) {
		List<AllMemberDto> list = adminService.allList();
		model.addAttribute("list", list);

		int memberCount = adminDao.memberCount(memberDto);
		model.addAttribute("memberCount", memberCount);

		int classCount = adminDao.classCount(classesDto);
		model.addAttribute("classCount", classCount);

		return "admin/allList";

	}

	// 소셜 + 아로리) 소셜조회
	@GetMapping("/aroriList")
	public String arorilist(Model model, Model model2, @ModelAttribute MemberDto memberDto,
			@ModelAttribute AroriMemberDto aroriMemberDto) {
		List<AllMemberDto> list = adminService.allList();
		model.addAttribute("list", list);

		int memberCount = adminDao.memberCount(memberDto);
		model.addAttribute("memberCount", memberCount);

		int aroriCount = adminDao.aroriCount(aroriMemberDto);
		model.addAttribute("aroriCount", aroriCount);

		return "admin/aroriList";

	}

	// 소셜 + 아로리) 소셜조회
	@GetMapping("/socialList")
	public String socialList(Model model, Model model2, @ModelAttribute MemberDto memberDto,
			@ModelAttribute AroriMemberDto aroriMemberDto) {

		List<AllMemberDto> list = adminService.allList();
		model.addAttribute("list", list);

		int memberCount = adminDao.memberCount(memberDto);
		model.addAttribute("memberCount", memberCount);

		int aroriCount = adminDao.aroriCount(aroriMemberDto);
		model.addAttribute("aroriCount", aroriCount);

		int socialCount = memberCount - aroriCount;
		model.addAttribute(socialCount);
		return "admin/socialList";

	}

	// 블랙멤버 리스트
	@GetMapping("/blacklist")
	public String blacklist(Model model, @ModelAttribute MemberDto memberDto, @ModelAttribute ReportDto reportDto) {

		List<AllMemberDto> list = adminService.allList();
		model.addAttribute("list", list);

		return "admin/blacklist";

	}

	// 클린멤버 리스트
	// 블랙리스트
	@GetMapping("/cleanList")
	public String cleanList(Model model, @ModelAttribute MemberDto memberDto, @ModelAttribute ReportDto reportDto) {

		List<AllMemberDto> list = adminService.allList();
		model.addAttribute("list", list);

		return "admin/cleanList";

	}
	// 검색

	@PostMapping("/search")
	public String search(@RequestParam String type, @RequestParam String keyword, Model model) {

		Map<String, String> param = new HashMap<>();
		param.put("type", type);
		param.put("keyword", keyword);
		List<MemberDto> list = sqlSession.selectList("admin.search", param);
		model.addAttribute("list", list);

		return "admin/allList";

	}

	// 소셜 + 아로리) 상세정보변경
	@GetMapping("/adminUpdate/{member_id}")
	public String adminUpdate(@PathVariable(required = false) String member_id, Model model) {
		// 변수가 없어도 적용가능

		AllMemberDto allMemberDto = memberDao.allGet(member_id);// 회원 아이디 단일조회해서
		model.addAttribute("allMemberDto", allMemberDto); // 페이지로 데이터 전달한다.

		return "admin/adminUpdate";
	}

	// 소셜 + 아로리) 상세정보변경
	@PostMapping("/adminUpdate/{member_id}")
	public String adminUpdate(@ModelAttribute AllMemberDto allMemberDto) {

		adminService.adminUpdate(allMemberDto);

		System.out.println("정보수정 성공");

		return "redirect:/";
	}

	// 소셜+아로리) 아우터조인 단일조회 + 클래스 개수 전달
	@GetMapping("/memberProfile/{member_no}")
	public String memberProfile(@PathVariable(required = false) int member_no, Model model, Model model2) {

		// 클래스 개수 전달
		int count = adminDao.classCount(member_no);
		model.addAttribute("count", count);

		This_imgDto this_imgDto = adminDao.getImage(member_no);
		model.addAttribute("this_imgDto", this_imgDto);

		AllMemberDto allMemberDto = memberDao.memberProfile(member_no);
		model.addAttribute("allMemberDto", allMemberDto); // 소셜회원 정보전달큐알체크못했는데 어떡
		// 아로리회원 정보전달

		return "admin/memberProfile";
	}

	// 회원 프로필 보기
	@PostMapping("/memberProfile/{member_no}")
	public String memberProfile(@PathVariable(required = false) int member_no, @ModelAttribute MemberDto memberDto,
			@ModelAttribute AroriMemberDto aroriMemberDto, @ModelAttribute This_imgDto this_imgDto) {

		adminService.getImage(member_no); // 멤버 이미지 불러오기

		adminService.memberProfile(member_no); // 소셜회원정보 get

		return "/memberProfile/{member_no}";
	}

	// 회원탈퇴 시키기
	@GetMapping("/delete")
	public String memberDelete(@ModelAttribute MemberDto memberDto) {

		adminService.delete(memberDto);

		return "admin/delete";
	}

}
