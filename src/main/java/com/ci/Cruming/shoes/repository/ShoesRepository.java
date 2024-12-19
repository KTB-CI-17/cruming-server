package com.ci.Cruming.shoes.repository;

import com.ci.Cruming.shoes.constants.FootType;
import com.ci.Cruming.shoes.constants.FootWidth;
import com.ci.Cruming.shoes.constants.ClimbingLevel;
import com.ci.Cruming.shoes.entity.Shoes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShoesRepository extends JpaRepository<Shoes, Long> {
    @Query("""
        SELECT s FROM Shoes s
        WHERE (:footType = 'EGYPTIAN' AND s.tip LIKE '%이집트형%')
           OR (:footType = 'GREEK' AND s.tip LIKE '%그리스형%')
           OR (:footType = 'ROMAN' AND s.tip LIKE '%로마형%')
        ORDER BY 
        CASE 
            WHEN :level = 'ELITE' AND s.tip LIKE '%고급자%' THEN 1
            WHEN :level = 'EXPERT' AND s.tip LIKE '%중상급자%' THEN 1
            WHEN :level = 'INTERMEDIATE' AND s.tip LIKE '%중급자%' THEN 1
            WHEN :level = 'AMATEUR' AND s.tip LIKE '%초급자%' THEN 1
            ELSE 2
        END,
        CASE 
            WHEN :footWidth = 'WIDE' AND s.tip LIKE '%넓은%' THEN 1
            WHEN :footWidth = 'NARROW' AND s.tip LIKE '%좁은%' THEN 1
            ELSE 2
        END
    """)
    List<Shoes> findRecommendedShoes(
        @Param("footType") FootType footType,
        @Param("footWidth") FootWidth footWidth,
        @Param("level") ClimbingLevel level
    );
} 