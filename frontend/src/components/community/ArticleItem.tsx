import React from 'react';
import { ArticleListItem } from '@/hooks/useArticle';
import { useDateFormat } from '@/hooks/useDateFormat';

interface ArticleItemProps {
  article: Omit<ArticleListItem, 'content'> | null; // article이 null일 수도 있음을 명시
  onClick?: () => void;
  tags?: string[];
}

const ArticleItem: React.FC<ArticleItemProps> = ({ 
  article,
  onClick,
  tags = []
}) => {
  // 커스텀 훅 사용
  const { formatDate } = useDateFormat();

  // article이 null이면 로딩 컴포넌트 또는 빈 상태 표시
  if (!article) {
    return <div className="bg-white p-4 rounded-lg border text-center">데이터를 불러오는 중...</div>;
  }

  // API 응답 변경: mcps는 이제 객체
  const mcpCategories = Object.keys(article.mcps || {});

  return (
    <div
      className="bg-white rounded-lg border flex flex-col cursor-pointer"
      style={{
        borderColor: '#E2E8F0',
        boxShadow: '0px 3px 16px 0px rgba(0,0,0,0.05)',
        background: '#FFF',
        padding: '12px 16px',
      }}
      onClick={onClick}
    >
      {/* 제목/공지/추천/댓글 */}
      <div className="flex items-center justify-between w-full">
        <div className="flex items-center">
          <span className="text-[1.1rem] text-[#222]">{article.title}</span>
          {/* API 응답 변경: isNotice → notice */}
          {article.notice && (
            <span
              className="ml-5 text-xs font-semibold"
              style={{
                borderRadius: 5,
                padding: '2px 8px',
                fontSize: '0.7rem',
                color: '#0095FF',
                background: '#E1F3FF',
                marginLeft: 5,
              }}
            >
              공지
            </span>
          )}
        </div>
        <div className="flex items-center gap-[15px]">
          <div className="flex items-center gap-1 text-[#2196F3]" style={{ fontSize: 18, fontWeight: 600 }}>
            {/* 엄지척(추천) 아이콘 */}
            <span>👍</span>
            <span>{article.recommendCount}</span>
          </div>
          <div className="flex items-center gap-1 text-[#2196F3]" style={{ fontSize: 18, fontWeight: 600 }}>
            {/* 말풍선(댓글) 아이콘 */}
            <span>🗨️</span>
            <span>{article.commentsCount || 0}</span>
          </div>
        </div>
      </div>
      {/* 작성자/날짜, 태그 */}
      <div className="flex flex-row mt-2 gap-10">
        <div className="flex flex-col" style={{ minWidth: 80 }}>
          <span className="text-[0.9rem] text-[#888] font-medium">
            {/* API 응답 변경: author 구조가 변경됨 */}
            {article.author?.name || '익명'}
          </span>
          <span className="text-[0.9rem] text-[#B0B0B0] mt-1">
            {formatDate(article.createdAt)}
          </span>
          <div className="flex flex-row flex-wrap gap-2 items-center mt-2">
            {tags && tags.length > 0 ? (
              tags.map((tag, idx) => (
                <span
                  key={idx}
                  className="text-[1rem]"
                  style={{
                    borderRadius: 20,
                    padding: '4px 8px',
                    color: '#555555',
                    background: '#EDEDED',
                    fontSize: '0.7rem',
                  }}
                >
                  {tag}
                </span>
              ))
            ) : mcpCategories.length > 0 ? (
              // mcps의 key 값을 태그로 표시
              mcpCategories.map((category, idx) => (
                <span
                  key={idx}
                  className="text-[1rem]"
                  style={{
                    borderRadius: 20,
                    padding: '4px 8px',
                    color: '#555555',
                    background: '#EDEDED',
                    fontSize: '0.7rem',
                  }}
                >
                  {category}
                </span>
              ))
            ) : null}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ArticleItem;