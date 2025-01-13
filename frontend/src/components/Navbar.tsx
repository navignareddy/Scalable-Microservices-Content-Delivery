import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { motion, AnimatePresence } from 'framer-motion';
import {
  HomeIcon,
  ChartBarIcon,
  CloudArrowUpIcon,
  FolderOpenIcon,
  UserCircleIcon,
  Bars3Icon,
  XMarkIcon,
  ArrowRightOnRectangleIcon
} from '@heroicons/react/24/outline';

import { useAuth } from '../context/AuthContext';

const NavbarContainer = styled.nav`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid ${props => props.theme.colors.gray[200]};
  padding: 0 1rem;
`;

const NavContent = styled.div`
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
`;

const Logo = styled(Link)`
  display: flex;
  align-items: center;
  font-size: 1.5rem;
  font-weight: bold;
  color: ${props => props.theme.colors.primary};
  text-decoration: none;
  
  &:hover {
    color: #2563EB;
  }
`;

const NavLinks = styled.div`
  display: flex;
  align-items: center;
  gap: 2rem;
  
  @media (max-width: 768px) {
    display: none;
  }
`;

const NavLink = styled(Link)<{ $isActive?: boolean }>`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: ${props => props.theme.borderRadius.md};
  color: ${props => props.$isActive ? props.theme.colors.primary : props.theme.colors.gray[600]};
  font-weight: ${props => props.$isActive ? '600' : '500'};
  transition: all 0.2s;
  
  svg {
    width: 20px;
    height: 20px;
  }
  
  &:hover {
    color: ${props => props.theme.colors.primary};
    background-color: ${props => props.theme.colors.gray[100]};
    transform: translateY(-1px);
  }
`;

const UserSection = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
  
  @media (max-width: 768px) {
    display: none;
  }
`;

const LogoutButton = styled.button`
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: transparent;
  color: ${props => props.theme.colors.gray[600]};
  border-radius: ${props => props.theme.borderRadius.md};
  transition: all 0.2s;
  
  svg {
    width: 20px;
    height: 20px;
  }
  
  &:hover {
    color: ${props => props.theme.colors.danger};
    background-color: ${props => props.theme.colors.gray[100]};
  }
`;

const MobileMenuButton = styled.button`
  display: none;
  align-items: center;
  justify-content: center;
  padding: 0.5rem;
  border-radius: ${props => props.theme.borderRadius.md};
  color: ${props => props.theme.colors.gray[600]};
  background-color: transparent;
  transition: all 0.2s;
  
  svg {
    width: 24px;
    height: 24px;
  }
  
  &:hover {
    background-color: ${props => props.theme.colors.gray[100]};
  }
  
  @media (max-width: 768px) {
    display: flex;
  }
`;

const MobileMenu = styled(motion.div)`
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border-bottom: 1px solid ${props => props.theme.colors.gray[200]};
  box-shadow: ${props => props.theme.shadows.lg};
  padding: 1rem;
  display: none;
  
  @media (max-width: 768px) {
    display: block;
  }
`;

const MobileNavLink = styled(Link)<{ $isActive?: boolean }>`
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  margin-bottom: 0.5rem;
  border-radius: ${props => props.theme.borderRadius.md};
  color: ${props => props.$isActive ? props.theme.colors.primary : props.theme.colors.gray[700]};
  font-weight: ${props => props.$isActive ? '600' : '500'};
  background-color: ${props => props.$isActive ? props.theme.colors.gray[100] : 'transparent'};
  transition: all 0.2s;
  
  svg {
    width: 20px;
    height: 20px;
  }
  
  &:hover {
    color: ${props => props.theme.colors.primary};
    background-color: ${props => props.theme.colors.gray[100]};
  }
`;

const MobileUserSection = styled.div`
  border-top: 1px solid ${props => props.theme.colors.gray[200]};
  padding-top: 1rem;
  margin-top: 1rem;
`;

const Navbar: React.FC = () => {
  const { isAuthenticated, user, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/');
    setIsMobileMenuOpen(false);
  };

  const navItems = [
    { path: '/dashboard', label: 'Dashboard', icon: HomeIcon, authRequired: true },
    { path: '/upload', label: 'Upload', icon: CloudArrowUpIcon, authRequired: true },
    { path: '/library', label: 'Library', icon: FolderOpenIcon, authRequired: true },
    { path: '/analytics', label: 'Analytics', icon: ChartBarIcon, authRequired: true },
    { path: '/profile', label: 'Profile', icon: UserCircleIcon, authRequired: true },
  ];

  const filteredNavItems = navItems.filter(item => 
    !item.authRequired || isAuthenticated
  );

  return (
    <NavbarContainer>
      <NavContent>
        <Logo to="/">
          <span>CDN System</span>
        </Logo>

        <NavLinks>
          {filteredNavItems.map(item => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.path}
                to={item.path}
                $isActive={location.pathname === item.path}
              >
                <Icon />
                {item.label}
              </NavLink>
            );
          })}
        </NavLinks>

        <UserSection>
          {isAuthenticated ? (
            <>
              <span>Welcome, {user?.firstName || 'User'}</span>
              <LogoutButton onClick={handleLogout}>
                <ArrowRightOnRectangleIcon />
                Logout
              </LogoutButton>
            </>
          ) : (
            <div style={{ display: 'flex', gap: '1rem' }}>
              <Link to="/login" className="btn btn-outline">Login</Link>
              <Link to="/register" className="btn btn-primary">Register</Link>
            </div>
          )}
        </UserSection>

        <MobileMenuButton onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}>
          {isMobileMenuOpen ? <XMarkIcon /> : <Bars3Icon />}
        </MobileMenuButton>
      </NavContent>

      <AnimatePresence>
        {isMobileMenuOpen && (
          <MobileMenu
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.2 }}
          >
            {filteredNavItems.map(item => {
              const Icon = item.icon;
              return (
                <MobileNavLink
                  key={item.path}
                  to={item.path}
                  $isActive={location.pathname === item.path}
                  onClick={() => setIsMobileMenuOpen(false)}
                >
                  <Icon />
                  {item.label}
                </MobileNavLink>
              );
            })}

            <MobileUserSection>
              {isAuthenticated ? (
                <>
                  <div style={{ padding: '0.75rem 1rem', color: '#6B7280' }}>
                    Welcome, {user?.firstName || 'User'}
                  </div>
                  <LogoutButton onClick={handleLogout} style={{ width: '100%', justifyContent: 'flex-start' }}>
                    <ArrowRightOnRectangleIcon />
                    Logout
                  </LogoutButton>
                </>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                  <Link 
                    to="/login" 
                    className="btn btn-outline" 
                    style={{ width: '100%' }}
                    onClick={() => setIsMobileMenuOpen(false)}
                  >
                    Login
                  </Link>
                  <Link 
                    to="/register" 
                    className="btn btn-primary" 
                    style={{ width: '100%' }}
                    onClick={() => setIsMobileMenuOpen(false)}
                  >
                    Register
                  </Link>
                </div>
              )}
            </MobileUserSection>
          </MobileMenu>
        )}
      </AnimatePresence>
    </NavbarContainer>
  );
};

export default Navbar; 