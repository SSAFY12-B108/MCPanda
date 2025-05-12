
interface ContentCardProps {
    title: string;
    tags: string[];
    likes: number;
}

const CombinationBox: React.FC<ContentCardProps> = ({ title, tags, likes }) => {
    return (
        <div className=" w-[200px] rounded-xl border border-gray-200 shadow-sm p-3 bg-white">
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
                    <span>{likes}</span>
                </div>
            </div>
        </div>
    );
};

export default CombinationBox;