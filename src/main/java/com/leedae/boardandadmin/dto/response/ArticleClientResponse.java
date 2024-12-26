package com.leedae.boardandadmin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leedae.boardandadmin.dto.ArticleDto;

import java.util.List;

public record ArticleClientResponse(
        @JsonProperty("_embedded") Embedded embedded,
        @JsonProperty("page") Page page
) {
    public static ArticleClientResponse empty(){
        return new ArticleClientResponse(
                new Embedded(List.of()),
                new Page(1,0,1,0)
        );
    }

    public static ArticleClientResponse of(List<ArticleDto> articles){
        return new ArticleClientResponse(
                new Embedded(articles),
                new Page(articles.size(), articles.size(),1,0)
        );
    }

    public List<ArticleDto> articles(){ return this.embedded().articles(); }

    public record Embedded(@JsonProperty("articles") List<ArticleDto> articles){}

    public record Page(
            int size,
            long totalElement,
            int totalPage,
            int number
    ){}
}
