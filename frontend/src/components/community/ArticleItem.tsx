// components/community/ArticleItem.tsx
import React from 'react';
import { ArticleListItem } from '@/hooks/useArticle';
import { useDateFormat } from '@/hooks/useDateFormat';

interface ArticleItemProps {
  article: Omit<ArticleListItem, 'content'>; // content만 제외 (mcps는 이미 올바른 타입)
  onClick?: () => void; // 게시글 클릭 이벤트 추가
  tags?: string[]; // 기존 컴포넌트에서 사용 중인 태그 속성
}

const ArticleItem: React.FC<ArticleItemProps> = ({ 
  article,
  onClick,
  tags = []
}) => {
  // 커스텀 훅 사용
  const { formatDate } = useDateFormat();
  
  return (
    <div
      className="bg-white rounded-lg border flex flex-col cursor-pointer"
      style={{
        borderColor: '#E2E8F0',
        boxShadow: '0px 3px 16px 0px rgba(0,0,0,0.05)',
        background: '#FFF',
        padding: '16px 20px',
      }}
      onClick={onClick}
    >
      {/* 제목/공지/추천/댓글 */}
      <div className="flex items-center justify-between w-full">
        <div className="flex items-center">
          <span className="text-[1.25rem] text-[#222]">{article.title}</span>
          {article.isNotice && (
            <span
              className="ml-5 text-xs font-semibold"
              style={{
                borderRadius: 5,
                padding: '2px 8px',
                fontSize: '0.75rem',
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
          <div className="flex items-center gap-1 text-[#2196F3]" style={{ fontSize: 20, fontWeight: 600 }}>
            {/* 엄지척(추천) 아이콘 */}
            <span>👍</span>
            <span>{article.recommendCount}</span>
          </div>
          <div className="flex items-center gap-1 text-[#2196F3]" style={{ fontSize: 20, fontWeight: 600 }}>
            {/* 말풍선(댓글) 아이콘 */}
            <span>🗨️</span>
            <span>{article.commentsCount || 0}</span>
          </div>
        </div>
      </div>
      {/* 작성자/날짜, 태그 */}
      <div className="flex flex-row mt-2 gap-10">
        <div className="flex flex-col" style={{ minWidth: 80 }}>
          <span className="text-[1rem] text-[#888] font-medium">
            {article.author.name || article.author.email}
          </span>
          <span className="text-[1rem] text-[#B0B0B0] mt-1">
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
                    fontSize: '0.75rem',
                  }}
                >
                  {tag}
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