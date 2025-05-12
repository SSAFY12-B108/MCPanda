import Link from 'next/link';

const Header: React.FC = () => {
  return (
    <header className="py-2.5 bg-white border-b border-gray-200 px-10">
      <div className="max-w-7xl mx-auto flex justify-between items-center">
        {/* Logo */}
        <Link href="/">
          <div className="text-2xl font-bold text-[#0095FF]">
            MCPanda
          </div>
        </Link>

        {/* Navigation */}
        <nav className="flex gap-6 text-sm text-gray-800 font-medium">
          <Link href="/community" className="hover:text-[#0095FF] transition">커뮤니티</Link>
          <Link href="/auth/login" className="hover:text-[#0095FF] transition">로그인</Link>
        </nav>
      </div>
    </header>
  );
};

export default Header;
