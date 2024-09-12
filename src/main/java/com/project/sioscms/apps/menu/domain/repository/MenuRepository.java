package com.project.sioscms.apps.menu.domain.repository;

import com.project.sioscms.apps.menu.domain.entity.Menu;
import com.project.sioscms.common.domain.repository.CommonJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MenuRepository extends CommonJpaRepository<Menu,Long> {
    Optional<Menu> findTop1ByIsDeletedOrderByOrderNumDesc(Boolean isDeleted);

    Optional<Menu> findTop1ByIsDeletedAndOrderNumOrderById(Boolean isDeleted, Long orderNum);

    Long countByUpperMenu_IdAndIsDeleted(Long upperMenuId, Boolean isDeleted);

    Set<Menu> findAllByIsDeletedOrderByOrderNumAsc(Boolean isDeleted);

    @Modifying
    /*@Query(value =
            "UPDATE Menu m " +
            "SET m.orderNum = m.orderNum + (:increaseNum) " +
            "WHERE m.orderNum >= :startOrderNum " +
            "AND m.orderNum <= :endOrderNum " +
            "AND m.orderNum <> :nowOrderNum " +
            "AND m.isDeleted = :isDeleted")*/
    @Query(name = "updateByOrders")
    void updateByOrders(Long nowOrderNum, Long startOrderNum, Long endOrderNum, Boolean isDeleted, Long increaseNum);

    @Query(value =
        "SELECT A " +
        "FROM Menu A " +
        "LEFT OUTER join AdminMenuAuth B ON B.menu.id = A.id " +
                "AND B.adminAuth.id = :adminAuthId " +
        "WHERE A.isDeleted = false " +
        "AND A.isRoot = false " +
        "AND B.isSelect = true " +
        "ORDER BY A.orderNum ASC ")
    List<Menu> findAllAdminMenus(Long adminAuthId);
}
