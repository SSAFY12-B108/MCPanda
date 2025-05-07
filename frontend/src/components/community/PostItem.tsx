// components/community/PostItem.tsx
import React from 'react';

interface PostItemProps {
  title: string;
  isNotice?: boolean;
  author: string;
  date: string;
  likes: number;
  comments: number;
  tags: string[];
  onClick?: () => void; // 게시글 클릭 이벤트 추가
}

const PostItem: React.FC<PostItemProps> = ({ 
  title, 
  isNotice, 
  author, 
  date, 
  likes, 
  comments, 
  tags,
  onClick 
}) => {
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
          <span className="text-[1.25rem] text-[#222]">{title}</span>
          {isNotice && (
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
            <svg width="20" height="20" fill="none" viewBox="0 0 24 24">
              <path d="M7 10V19C7 19.5523 7.44772 20 8 20H17C17.5523 20 18 19.5523 18 19V12C18 11.4477 17.5523 11 17 11H12.382C12.1381 11 11.9045 10.8946 11.7322 10.7071L10.4142 9.29289C10.1492 9.00936 10 8.63261 10 8.24264V6C10 5.44772 10.4477 5 11 5C11.5523 5 12 5.44772 12 6V8" stroke="#2196F3" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              <rect x="4" y="10" width="3" height="10" rx="1.5" stroke="#2196F3" strokeWidth="2"/>
            </svg>
            <span>{likes}</span>
          </div>
          <div className="flex items-center gap-1 text-[#2196F3]" style={{ fontSize: 20, fontWeight: 600 }}>
            {/* 말풍선(댓글) 아이콘 */}
            <svg width="20" height="20" fill="none" viewBox="0 0 24 24"><path stroke="#2196F3" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" d="M21 6.5A2.5 2.5 0 0 0 18.5 4h-13A2.5 2.5 0 0 0 3 6.5v7A2.5 2.5 0 0 0 5.5 16H6v3.28a.75.75 0 0 0 1.28.53L11.06 16h7.44A2.5 2.5 0 0 0 21 13.5v-7Z"/></svg>
            <span>{comments}</span>
          </div>
        </div>
      </div>
      {/* 작성자/날짜, 태그 */}
      <div className="flex flex-row mt-2 gap-10">
        <div className="flex flex-col" style={{ minWidth: 80 }}>
          <span className="text-[1rem] text-[#888] font-medium">{author}</span>
          <span className="text-[1rem] text-[#B0B0B0] mt-1">{date}</span>
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

export default PostItem;