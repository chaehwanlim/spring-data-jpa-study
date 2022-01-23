package spring.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import spring.datajpa.entity.Member;
import spring.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();

        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMemberWithDomainClassConverter(@PathVariable("id") Member member) {
        // 스프링이 바로 member를 조회한다.
        return member.getUsername();
    }

    @PostConstruct
    public void init() {
        memberRepository.save(Member.builder().username("memberA").build());
    }

}
