package spring.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spring.datajpa.entity.Member;
import spring.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    void test1() {
        Member member = Member.builder().username("memberA").build();
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).orElse(null);

        assert findMember != null;
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member memberA = Member.builder().username("memberA").build();
        Member memberB = Member.builder().username("memberB").build();

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // 단일 조회 검증
        Member findMemberA = memberRepository.findById(memberA.getId()).get();
        Member findMemberB = memberRepository.findById(memberB.getId()).get();
        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all).isEqualTo(List.of(memberA, memberB));

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(memberA);
        memberRepository.delete(memberA);
        long countAfterDeletion = memberRepository.count();
        assertThat(countAfterDeletion).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("AAA").age(20).build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("AAA").age(20).build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("AAA").age(20).build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("AAA").age(20).build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        memberRepository.findUsernameList().forEach(username -> {
            System.out.println("username = " + username);
        });
    }

    @Test
    public void findMemberDto() {
        Team t1 = Team.builder().name("teamA").build();
        teamRepository.save(t1);

        Member m1 = Member.builder().username("AAA").age(10).build();
        m1.changeTeam(t1);
        memberRepository.save(m1);

        memberRepository.findMemberDto().forEach(memberDto -> {
            System.out.println("dto = " + memberDto);
        });
    }

    @Test
    public void findByNames() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("BBB").age(20).build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        memberRepository.findByNames(List.of("AAA", "BBB")).forEach(username -> {
            System.out.println("username = " + username);
        });
    }

    @Test
    public void returnTypeTest() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("AAA").age(20).build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        Member aaa1 = memberRepository.findMemberByUsername("AAA");
        Optional<Member> aaa2 = memberRepository.findOptionalMemberByUsername("AAA");
    }

}