package com.company.usertradersback.repository;

import com.company.usertradersback.entity.*;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.util.List;

@Repository
public class BoardQueryDsl {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public String selectPath(Integer boardId) {

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        QBoardImageEntity qBoardImageEntity = QBoardImageEntity.boardImageEntity;

        try {
            List<BoardImageEntity> boardImageEntities = jpaQueryFactory.selectFrom(qBoardImageEntity)
                    .where(qBoardImageEntity.boardId.id.eq(boardId)).fetch();

            return boardImageEntities.get(0).getPath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public String selectGrade(Integer userId) {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        QUserGradesEntity qUserGradesEntity = QUserGradesEntity.userGradesEntity;
        try {
            Long valid = jpaQueryFactory.selectFrom(qUserGradesEntity)
                    .where(qUserGradesEntity.userRecvId.id.eq(userId)).fetchCount();
            if (valid > 0) {

                QueryResults<UserGradesEntity> userGradesEntities = jpaQueryFactory.selectFrom(qUserGradesEntity)
                        .where(qUserGradesEntity.userRecvId.id.eq(userId)).fetchResults();

                int sum = 0;
                int count = userGradesEntities.getResults().size();
                for (int i = 0; i < count; i++) {
                    sum += userGradesEntities.getResults().get(i).getGrade();
                }
                double a = 0;
                a = Math.round((sum / (double) count) * 100) / 100.0;

                return new DecimalFormat("0.00").format(a);
            } else {
                return new DecimalFormat("0.00").format(0.00);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public Boolean existLikeWhether(Integer boardId, Integer userId) {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        QBoardLikeUserEntity qBoardLikeUserEntity = QBoardLikeUserEntity.boardLikeUserEntity;
        try {
            Boolean likeWhether = jpaQueryFactory.select(jpaQueryFactory.selectOne()
                    .from(qBoardLikeUserEntity)
                    .where(qBoardLikeUserEntity.boardId.id.eq(boardId), qBoardLikeUserEntity.userId.id.eq(userId))
                    .fetchAll().exists()
            ).from(qBoardLikeUserEntity).fetchOne();
            System.out.println(likeWhether);
            return likeWhether;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
