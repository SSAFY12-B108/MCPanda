
interface ContentCardProps {
    title: string;
    tags: string[];
    recommendCount: number;
    onClick?: () => void; // 클릭 이벤트 핸들러 추가
}

const CombinationBox: React.FC<ContentCardProps> = ({ title, tags, recommendCount, onClick }) => {
    return (
        <div className=" w-[200px] rounded-xl border border-gray-200 shadow-sm p-3 bg-white hover:shadow-md transition-shadow mb-3 cursor-pointer" onClick={onClick}>
            <h2 className="text-sm font-medium text-gray-900 mb-4">{title}</h2>
            {/* 태그 + 좋아요를 한 줄에 정렬 */}
            <div className="flex justify-between ">
                <div className="flex flex-wrap gap-2">
                    {tags.map((tag) => (
                        <span
                            key={tag}
                            className="text-xs font-semibold px-3 py-1 rounded-full"
                            style={{
                                backgroundColor: '#E6F4FF', // 배경은 연한 버전으로
                                color: '#0095FF',           // 텍스트 색상
                            }}
                        >
                            {tag}
                        </span>
                    ))}
                </div>
                <div className="flex  text-sm text-gray-700 items-end">
                    👍
                    <span>{recommendCount}</span>
                </div>
            </div>
        </div>
    );
};

export default CombinationBox;