package team2.pjt12.matchumoney.domain.saving.codef.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.saving.codef.domain.ConnectedIdVO;

import java.util.List;

@Mapper
public interface CodefMapper {
    void insertCodefConnectedId(ConnectedIdVO connectedIdVO);

    String getCodefConnectedIdByUserId(Long userId);

    boolean deleteCodefConnectedIdByUserId(Long userId);

    void insertCodefConnectedIdOrganization(
            @Param("connectedId") String connectedId,
            @Param("organization") String organization
    );

    //연결된 은행 목록
    List<String> selectOrganizationNamesByUserId(@Param("userId") Long userId);


}
