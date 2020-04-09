package com.visionvera.dao.xinhxu;

import com.visionvera.vo.LabelVO;

import java.awt.*;
import java.util.List;

public interface LabelDao {

    /**
     * @Description TODO 添加标签
     * @param labelVO
     * @return: void
     * @Author: Xinhxu
     * @Date: 14:02 2020/4/2
     */

    int addLabel(LabelVO labelVO);

    /**
     * @Description TODO 获取标签
     * @param labelVO
     * @return: java.util.List<com.visionvera.vo.ArticleVO>
     * @Author: Xinhxu
     * @Date: 14:03 2020/4/2
     */

    List<Label> getLabelList(LabelVO labelVO);

    /**
     * @Description TODO 修改文章
     * @param labelVO
     * @return: void
     * @Author: Xinhxu
     * @Date: 14:41 2020/4/2
     */

    void updateLabel(LabelVO labelVO);

}
