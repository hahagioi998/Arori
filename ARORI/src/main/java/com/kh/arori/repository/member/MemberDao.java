package com.kh.arori.repository.member;

import java.util.List;
import java.util.Map;

import com.kh.arori.entity.AroriMemberDto;
import com.kh.arori.entity.MemberDto;
import com.kh.arori.entity.PasswordQDto;

public interface MemberDao {

   // 회원 시퀀스 발급
   public int getSeq();
   
   // 비밀번호 질문 받아오기
   public List<PasswordQDto> getPasswordQ();
   
   // 회원 단일 조회
   public MemberDto get(String member_id);
   
   // 전체 회원 조회 
   public List<MemberDto> getList();
   
   // 회원 가입 > MEMBER TABLE
   public void join(MemberDto memberDto);
   
   // 아로리) 회원 가입
   public void joinArori(AroriMemberDto aroriMemberDto);
   
   // 아로리) 회원 상세 정보 가지고 오기 
   public AroriMemberDto getArori(String member_id);
   
   // 로그인 시 로그인 시간 갱신 
   public int loginSuccess(String member_id);
   
   // 아로리) 회원 아이디 찾기 
   public MemberDto findId(AroriMemberDto aroriMemberDto);
   
   // 아로리) 회원 비밀번호 찾기 > 반환 : member_email
   public String findPw(Map<String, String> findPw);
   
   // 아로리) 회원 비밀번호 > 임시 비밀번호로 변경 
   public int changeTempPw(AroriMemberDto aroriMemberDto);
   
   // 아로리) 회원정보 수정 (윤아)
   public void updateArori(AroriMemberDto aroriMemberDto);

   // 아로리) 마이페이지
   public AroriMemberDto myInfo(int member_no);
   
   //아로리) 회원 조회 (윤아)
   public List<AroriMemberDto> getAroriList();
   
   //아로리) 비밀번호체크
   public boolean checkPw(String member_pw);

   //소셜회원) 정보수정
   public void updateSocial(MemberDto memberDto);
   
   //소셜회원)마이페이지
   public MemberDto SocialInfo(int member_no);

   //중복 아이디 검사 
   public MemberDto checkOverlap(String member_id);
   //중복메일 검사
    public MemberDto checkOverlapMail(String member_email);
    //중복 닉네임 검사 
	public MemberDto checkOverlapNick(String member_nick);
	//중복 폰번호 검사 
	public MemberDto checkOverlapPhone(String member_phone);
    
}