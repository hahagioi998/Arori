package com.kh.arori.service.member;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.arori.entity.AroriMemberDto;
import com.kh.arori.entity.MemberDto;
import com.kh.arori.repository.member.MemberDao;
import com.kh.arori.service.email.EmailService;

@Service
public class MemberServiceImple implements MemberService {

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private EmailService emailService;

	// 시퀀스 발급
	@Override
	public int getSeq() {
		int member_no = memberDao.getSeq();
		return member_no;
	}

	// (공통) 회원 가입
	@Override
	@Transactional
	public void join(String emailPath, MemberDto memberDto) {

		// 1. 시퀀스 발급
		int member_no = this.getSeq();

		memberDto.setMember_no(member_no);

		// 2. 소셜 회원 > Google ? Github
		if (emailPath.equals("google.com")) {
			// 구글
			memberDto.setMember_state("GOOGLE");
		} else {
			// 구글 외 이메일 (Github)
			memberDto.setMember_state("GITHUB");
		}

		// 3. 회원 가입
		memberDao.join(memberDto);
		
		System.out.println("Service");

	}

	// (아로리) 회원 가입
	@Override
	@Transactional
	public void join(MemberDto memberDto, AroriMemberDto aroriMemberDto) {

		// 1. 시퀀스 발급
		int member_no = this.getSeq();

		// 2. 비밀번호 암호화
		aroriMemberDto.setMember_pw(encoder.encode(aroriMemberDto.getMember_pw()));

		// 3. memberDto / aroriMemberDto 에 시퀀스 번호 및 emailPath(ARORI) 삽입
		memberDto.setMember_no(member_no);
		memberDto.setMember_state("ARORI");
		aroriMemberDto.setMember_no(member_no);

		// 4. 해당 정보 MEMBER 테이블에 삽입
		memberDao.join(memberDto);

		// 5. 아로리 회원 정보 ARORI_MEMBER 테이블에 삽입
		memberDao.joinArori(aroriMemberDto);
	}

	// 아로리 회원 로그인
	@Override
	public MemberDto aroriLogin(String member_id, String member_pw) {
		
		AroriMemberDto aroriMember = memberDao.getArori(member_id);
		
		boolean pass = false;
		
		if(aroriMember != null) {			
			pass = encoder.matches(member_pw, aroriMember.getMember_pw());
		}
		
		MemberDto member;
		
		if(pass) {
			member = this.loginSuccess(member_id);
			return member;
		}
		
		
		return member = null;
	}

	// (공통) 로그인 갱신
	@Override
	public MemberDto loginSuccess(String member_id) {

		int updateResult = memberDao.loginSuccess(member_id);

		MemberDto member;

		if (updateResult == 1) {
			member = memberDao.get(member_id);
			return member;
		}

		return member = null;
	}

	// 아로리) 비밀번호 찾기 > 이메일 보내기 
	@Override
	@Transactional
	public String findPw(String member_id, String member_q, String member_a) throws Exception {
		// 1. 파라미터 값 > Map 으로 객체 통합 
		Map<String, String> findPw = new HashMap<String, String>();
		findPw.put("member_id", member_id);
		findPw.put("member_q", member_q);
		findPw.put("member_a", member_a);
		
		// 2. 회원 존재 여부 확인 
		String email = memberDao.findPw(findPw);
		
		// 3. 찾을 수 없는 회원이면 null 반환 
		if(email.isEmpty()) {
			return null;
		}
		
		// 4. 임시 비밀번호 발급 
		String temporaryPw= this.temporaryPw();
		AroriMemberDto member = AroriMemberDto.builder().member_email(email).member_pw(encoder.encode(temporaryPw)).build();
		
		// 5. 임시 비밀번호로 회원 비밀번호 변경 
		memberDao.changeTempPw(member);
		
		// 6. 해당 회원의 이메일로 임시 비밀번호 전송 
		Map<String, String> emailAndTempPw = new HashMap<String, String>();
		emailAndTempPw.put("email", email);
		emailAndTempPw.put("tempPw", temporaryPw);
		
		emailService.sendPassword(emailAndTempPw);
		
		return email;
	}

	// 아로리) 임시 비밀번호 발급 
	@Override
	public String temporaryPw() {
		String temporaryPw = UUID.randomUUID().toString().substring(0, 8);
		return temporaryPw;
	}

}
