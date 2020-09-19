package com.kh.arori.repository.admin;


import java.util.List;
import java.util.Map;

import com.kh.arori.entity.img.This_imgDto;
import com.kh.arori.entity.member.AllMemberDto;
import com.kh.arori.entity.member.AroriMemberDto;
import com.kh.arori.entity.member.MemberDto;
import com.kh.arori.entity.member.ReportDto;
import com.kh.arori.entity.study.ClassesDto;


public interface AdminDao {

	
	
	//회원1명당 클래스 개수조회
	public int classCount(int member_no);
	
	//페이지네이션 회원 카운트
	public int totalCnt();
	
	//클래스목록(차트)
	public List<ClassesDto>getIncome();
	
	//회원+회원이미지 불러오기
	public This_imgDto getImage(int member_no);
	
	//클래스 총 개수
	public int classCount(ClassesDto classesDto);
	
	//멤버 총 인원
	public int memberCount();

	//비밀번호 확인
	public int checkPw(AroriMemberDto aroriMemberDto);
	
	//아로리 총 인원
	public int aroriCount(AroriMemberDto aroriMemberDto);
	
	//아로리 총 멤버
	public List<AllMemberDto>allList();
	
	//회원 탈퇴
	public void delete(int member_no);
	
	// 페이지 네이션 
	public List<AllMemberDto> page(Map<String, Integer> pagination);
	

}
