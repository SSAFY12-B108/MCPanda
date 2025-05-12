// hooks/useDateFormat.ts
import { useMemo } from "react";

export const useDateFormat = () => {
  const formatDate = useMemo(() => {
    return (dateString: string): string => {
      try {
        const date = new Date(dateString);
        
        // 유효하지 않은 날짜면 원본 문자열 반환
        if (isNaN(date.getTime())) {
          return dateString;
        }
        
        // YYYY.MM.DD 형식으로 변환
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        
        return `${year}.${month}.${day}`;
      } catch (error) {
        console.error('Date formatting error:', error);
        return dateString;
      }
    };
  }, []);
  
  return { formatDate };
};