package spring.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import spring.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = Team.builder().name("teamA").build();
        Team teamB = Team.builder().name("teamB").build();

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = Member.builder()
                .username("member1")
                .age(10)
                .team(teamA)
                .build();

        Member member2 = Member.builder()
                .username("member2")
                .age(10)
                .team(teamB)
                .build();

        Member member3 = Member.builder()
                .username("member3")
                .age(10)
                .team(teamA)
                .build();

        Member member4 = Member.builder()
                .username("member4")
                .age(10)
                .team(teamB)
                .build();

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        // 확인
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam() = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBaseEntity() throws Exception {
        // given
        Member memberA = Member.builder().username("memberA").build();
        memberRepository.save(memberA);

        Thread.sleep(100);
        memberA.updateUsername("memberB");

        // when
        Member foundMember = memberRepository.findById(memberA.getId()).get();
        System.out.println("foundMember = " + foundMember);

        // then
        System.out.println("foundMember.createdDate = " + foundMember.getCreatedDate());
        System.out.println("foundMember.updatedDate = " + foundMember.getUpdatedDate());
    }

}