package com.dxxbjl.community.dao.elasticsearch;

import com.dxxbjl.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository extends ElasticsearchCrudRepository<DiscussPost,Integer> {

}
